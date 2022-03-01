package com.demo.inbox.contollers;

import com.demo.inbox.email.Email;
import com.demo.inbox.email.EmailRepository;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping("/emails/{id}")
    public String getEmailPage(@AuthenticationPrincipal OAuth2User principal,
                               Model model,
                               @PathVariable UUID id) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("name"))) {
            return "index";
        }

        String userId = principal.getAttribute("login");

        //fetch and add Default and User folders to model
        List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);

        // fetch unread email counts from unread_email_count_by_user_folder
        Map<String, Integer> folderToUnreadCounts = folderService.getFolderToUnreadCounts(userId);
        model.addAttribute("folderToUnreadCounts", folderToUnreadCounts);

        //fetch email by given input id
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (!optionalEmail.isPresent()) {
            return "index";
        }
        Email email = optionalEmail.get();
        model.addAttribute("email", email);

        //converting list of to ids to comma separated string and add to model
        List<String> toIds = email.getTo();
        String toIdsString = String.join(",", toIds);
        model.addAttribute("toIdsString", toIdsString);

        return "email-page";
    }
}
