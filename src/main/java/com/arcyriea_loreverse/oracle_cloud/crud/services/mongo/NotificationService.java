package com.arcyriea_loreverse.oracle_cloud.crud.services.mongo;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mongo.NotificationDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Notifications;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mongo.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // CREATE
    public Notifications create(NotificationDTOs.NotificationSubmit dto) {
        Notifications notification = new Notifications();
        notification.setAuthor(dto.author());
        notification.setMessage(dto.message());
        notification.setTimestamp(Instant.now());
        return notificationRepository.save(notification);
    }

    // READ (All)
    public List<Notifications> findAll() {
        return notificationRepository.findAll();
    }

    // READ (Single by ID)
    public Optional<Notifications> findById(String id) {
        return notificationRepository.findById(id);
    }

    // UPDATE
    public Notifications update(String id, NotificationDTOs.NotificationSubmit dto) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setAuthor(dto.author());
            notification.setMessage(dto.message());
            return notificationRepository.save(notification);
        }).orElseThrow(() -> new RuntimeException("Notification not found with id " + id));
    }

    // DELETE
    public void delete(String id) {
        notificationRepository.deleteById(id);
    }
}