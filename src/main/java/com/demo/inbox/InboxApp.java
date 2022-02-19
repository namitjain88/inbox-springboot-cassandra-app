package com.demo.inbox;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.inbox.email.Email;
import com.demo.inbox.email.EmailRepository;
import com.demo.inbox.emaillist.EmailItemListRepository;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.emaillist.EmailListItemKey;
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

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.Arrays;

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
    private EmailItemListRepository emailItemListRepository;

    @Autowired
    private EmailRepository emailRepository;

    @PostConstruct
    public void init() {
        folderRepository.save(new Folder("namitjain88", "Inbox", "blue"));
        folderRepository.save(new Folder("namitjain88", "Sent", "green"));
        folderRepository.save(new Folder("namitjain88", "Important", "yellow"));

        for (int i = 0; i < 10; i++) {
            EmailListItemKey key = new EmailListItemKey();
            key.setUserId("namitjain88");
            key.setLabel("Inbox");
            key.setTimeUUID(Uuids.timeBased());

            EmailListItem emailListItem = new EmailListItem();
            emailListItem.setKey(key);
            emailListItem.setTo(Arrays.asList("namitjain88", "abc", "xyz"));
            emailListItem.setSubject("Subject - " + i);
            emailListItem.setRead(true);

            emailItemListRepository.save(emailListItem);

            //seeding email messages
            Email email = new Email();
            email.setId(key.getTimeUUID());
            email.setFrom(key.getUserId());
            email.setTo(emailListItem.getTo());
            email.setSubject(emailListItem.getSubject());
            email.setBody("Body " + i);

            emailRepository.save(email);
        }
    }
}
