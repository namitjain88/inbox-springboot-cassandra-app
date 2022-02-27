package com.demo.inbox.contollers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.demo.inbox.email.EmailService;
import com.demo.inbox.folder.Folder;
import com.demo.inbox.folder.FolderRepository;
import com.demo.inbox.folder.FolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;    

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

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

        model.addAttribute("toIds", String.join(", ", createListOfRecipients(to)));

        if (StringUtils.hasText(subject)) {
            model.addAttribute("subject", "Re: " + subject);
        }

        return "compose-page";
    }

    @PostMapping("/send")
    public ModelAndView sendEmail(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestBody MultiValueMap<String, String> formData) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return new ModelAndView("redirect:/");
        }

        String userId = principal.getAttribute("login");

        // Extract field values from formData
        String toIds = formData.getFirst("toIds");
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");

        List<String> recipients = createListOfRecipients(toIds);
        emailService.sendEmail(userId, recipients, subject, body);

        return new ModelAndView("redirect:/");
    }

    private List<String> createListOfRecipients(String toIds) {

        if (!StringUtils.hasText(toIds)) {
            return new ArrayList<>();
        }

        List<String> recipients = Arrays.asList(toIds.split(",")).stream()
                .map(StringUtils::trimWhitespace)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        return recipients;
    }
}
