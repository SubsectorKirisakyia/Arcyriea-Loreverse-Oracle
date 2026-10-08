package com.arcyriea_loreverse.oracle_cloud.crud.services.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo.UnifiedChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnifiedChatService {
    @Autowired
    private final UnifiedChatRepository unifiedChatRepository;

    public UnifiedChatService(UnifiedChatRepository unifiedChatRepository) {
        this.unifiedChatRepository = unifiedChatRepository;
    }

    public Chats save(Chats chat) {
        return unifiedChatRepository.save(chat);
    }

    public List<Chats> findAll(){
        return unifiedChatRepository.findAll();
    }

    public List<Chats> findTop10ByOrderByTimestampDesc() {
        return unifiedChatRepository.findTop10ByOrderByTimestampDesc();
    }
}
