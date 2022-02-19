package com.demo.inbox.contollers;

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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @GetMapping("/compose")
    public String getComposePage(
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String subject,
            @AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        // Fetch folders
        String userId = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);

        List<Folder> defaultFolders = folderService.getDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        if (StringUtils.hasText(to)) {
            String[] toIdsArray = to.split(",");
            List<String> toIds = Arrays.asList(toIdsArray).stream()
                    .map(toId -> StringUtils.trimWhitespace(toId))
                    .distinct()
                    .collect(Collectors.toList());
            model.addAttribute("toIds", String.join(",", toIds));
        }

        if (StringUtils.hasText(subject)) {
            model.addAttribute("subject", "Re: " + subject);
        }

        return "compose-page";
    }
}
