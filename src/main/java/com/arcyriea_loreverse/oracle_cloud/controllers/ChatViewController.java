package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/chat")
@RequiredArgsConstructor
public class ChatViewController {
    //For post streams moderation logging
    private final UnifiedChatService service;

    @GetMapping("/fetch")
    public List<Chats> fetchAllChats(){
        return service.findAll();
    }

    @GetMapping("/recent")
    public List<Chats> getRecentChats() {
        // Fetch the most recent 10 messages from MongoDB
        // Note: You'll need a method in your repository that sorts by timestamp descending
        return service.findTop10ByOrderByTimestampDesc();
    }
}
