package com.arcyriea_loreverse.oracle_cloud.crud.services.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo.UnifiedChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnifiedChatService {
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
}
