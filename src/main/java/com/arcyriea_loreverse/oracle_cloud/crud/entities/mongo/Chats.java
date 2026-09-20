package com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chats {
    @Id
    private String id;
    @Indexed
    private String username;
    @Indexed
    private String source;
    @TextIndexed
    private String message;
    private Instant timestamp;
}
