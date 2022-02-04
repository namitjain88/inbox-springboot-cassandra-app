package com.demo.inbox.folder;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class FolderService {

    public List<Folder> getDefaultFolders(String userId){
        return Arrays.asList(
                new Folder(userId,"Inbox","blue"),
                new Folder(userId,"Sent Items","green"),
                new Folder(userId,"Important","red")
        );
    }
}
