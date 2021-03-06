package com.demo.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import com.demo.inbox.email.EmailService;
import com.demo.inbox.folder.Folder;
import com.demo.inbox.folder.FolderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class InboxApp {

    public static void main(String[] args) {
        SpringApplication.run(InboxApp.class, args);
    }

    @RequestMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println(principal);
        return principal.getAttribute("name");
    }

    /**
     * This is necessary to have the Spring Boot app use the Astra secure bundle to connect to database
     */
    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("namitjain88", "Work", "blue"));
        folderRepository.save(new Folder("namitjain88", "Personal", "green"));
        folderRepository.save(new Folder("namitjain88", "Family", "yellow"));

        for (int i = 0; i < 10; i++) {
            emailService.sendEmail("namitjain88", Arrays.asList("namitjain88", "abc", "xyz"), "Subject " + i, "Body " + i);
        }
    }
}
