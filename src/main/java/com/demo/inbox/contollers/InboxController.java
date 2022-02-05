package com.demo.inbox.contollers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.inbox.emaillist.EmailItemListRepository;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.folder.Folder;
import com.demo.inbox.folder.FolderRepository;
import com.demo.inbox.folder.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailItemListRepository emailItemListRepository;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model,
                           @RequestParam(required = false) String folder) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("name"))) {
            return "index";
        }

        // fetch folders by user
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);

        List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        if (!StringUtils.hasText(folder)) {
            folder = "Inbox";
        }
        model.addAttribute("folderLabel", folder);

        // fetch emailList by user and folder
        List<EmailListItem> emailList = emailItemListRepository.findAllByKey_UserIdAndKey_Label(userId, folder);
        model.addAttribute("emailList", emailList);

        PrettyTime prettyTime = new PrettyTime();
        emailList.forEach(emailItem -> {
            UUID timeUuid = emailItem.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
            emailItem.setAgoTimeString(prettyTime.format(emailDateTime));
        });

        return "inbox-page";
    }
}
