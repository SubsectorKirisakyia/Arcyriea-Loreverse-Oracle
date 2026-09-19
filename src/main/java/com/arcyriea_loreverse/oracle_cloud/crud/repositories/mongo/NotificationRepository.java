package com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Notifications;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notifications, String> {

}
