package com.arcyriea_loreverse.oracle_cloud.crud.services.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Notifications;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notifications save(Notifications notifications){
        return notificationRepository.save(notifications);
    }
}
