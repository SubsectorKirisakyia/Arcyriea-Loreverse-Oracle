package com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chats")
@Getter
@Setter
public class Chats {
    @MongoId
    private String id;
    private String username;
    private String source;
    private String message;
    private Instant timestamp;
}
