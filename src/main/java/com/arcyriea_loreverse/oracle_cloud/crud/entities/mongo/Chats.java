package com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo;

import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chats")
public class Chats {
    @MongoId
    private String id;
    private String username;
    private String source;
    private String message;
    private Instant timestamp;
}
