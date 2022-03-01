package com.demo.inbox.folder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    @Autowired
    private UnreadEmailCountRepository unreadEmailCountRepository;

    public List<Folder> getDefaultFolders(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "blue"),
                new Folder(userId, "Sent Items", "green"),
                new Folder(userId, "Important", "red"));
    }

    public Map<String, Integer> getFolderToUnreadCounts(String userId) {
        List<UnreadEmailCount> unreadEmailCounts = unreadEmailCountRepository.findAllByUserId(userId);
        return unreadEmailCounts.stream()
                .collect(Collectors.toMap(UnreadEmailCount::getLabel, UnreadEmailCount::getUnreadEmailCount));
    }
}
