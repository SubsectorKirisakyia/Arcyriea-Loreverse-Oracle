package com.arcyriea_loreverse.oracle_cloud.crud.services.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo.UnifiedChatRepository;
import org.springframework.stereotype.Service;

@Service
public class UnifiedChatService {
    private final UnifiedChatRepository unifiedChatRepository;

    public UnifiedChatService(UnifiedChatRepository unifiedChatRepository) {
        this.unifiedChatRepository = unifiedChatRepository;
    }
}
