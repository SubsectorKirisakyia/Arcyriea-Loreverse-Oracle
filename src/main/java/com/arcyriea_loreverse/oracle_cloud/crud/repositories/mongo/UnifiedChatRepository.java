package com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UnifiedChatRepository extends MongoRepository<Chats, String> {
    List<Chats> findBySource(String source);
    List<Chats> findByUsername(String username);
}
