package com.arcyriea_loreverse.oracle_cloud.configs.datasources;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo")
public class MongoConfig {
    // Configuration
}
