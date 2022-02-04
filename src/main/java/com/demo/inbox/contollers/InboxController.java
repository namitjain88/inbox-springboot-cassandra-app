package com.demo.inbox.contollers;

import com.demo.inbox.emaillist.EmailItemListRepository;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.folder.Folder;
import com.demo.inbox.folder.FolderRepository;
import com.demo.inbox.folder.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailItemListRepository emailItemListRepository;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("name"))) {
            return "index";
        }

        // fetch folders by user
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);

        List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        // fetch emailList by user and folder
        String folderLabel = "Inbox";
        List<EmailListItem> emailList = emailItemListRepository.findAllByKey_UserIdAndKey_Label(userId, folderLabel);
        model.addAttribute("emailList", emailList);

        return "inbox-page";
    }
}
