package com.demo.inbox.email;

import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.inbox.emaillist.EmailItemListRepository;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.emaillist.EmailListItemKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailItemListRepository emailItemListRepository;

    public void sendEmail(String sender, List<String> recipients, String subject, String body) {

        // 1. Create the message in messages_by_id table
        Email email = new Email();
        email.setId(Uuids.timeBased());
        email.setFrom(sender);
        email.setTo(recipients);
        email.setSubject(subject);
        email.setBody(body);

        emailRepository.save(email);

        // 2. Create message in messages_by_user_folder for all recipients; folder will
        // be Inbox
        recipients.forEach(toId -> {
            EmailListItem emailListItem = createEmailListItem(toId, "Inbox", recipients, subject, email.getId());
            emailItemListRepository.save(emailListItem);
        });

        // 3. Create the message in messages_by_user_folder for sender; folder will be
        // Sent Items
        EmailListItem sentItemsEntry = createEmailListItem(sender, "Sent Items", recipients, subject, email.getId());

        emailItemListRepository.save(sentItemsEntry);
    }

    private EmailListItem createEmailListItem(String ownerId, String folder, List<String> recipients, String subject,
            UUID messageId) {
        EmailListItemKey emailListItemKey = new EmailListItemKey();
        emailListItemKey.setUserId(ownerId);
        emailListItemKey.setLabel(folder);
        emailListItemKey.setTimeUUID(messageId);

        EmailListItem emailListItem = new EmailListItem();
        emailListItem.setKey(emailListItemKey);
        emailListItem.setSubject(subject);
        emailListItem.setTo(recipients);
        emailListItem.setRead(false);
        return emailListItem;
    }
}
