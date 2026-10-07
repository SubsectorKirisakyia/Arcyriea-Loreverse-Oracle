package com.arcyriea_loreverse.oracle_cloud.controllers.transients;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mongo.NotificationDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Notifications;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationDTOs.NotificationRetrieve> create(@Valid @RequestBody NotificationDTOs.NotificationSubmit dto) {
        Notifications saved = notificationService.create(dto);
        return new ResponseEntity<>(mapToRetrieveDTO(saved), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTOs.NotificationRetrieve>> getAll() {
        List<NotificationDTOs.NotificationRetrieve> list = notificationService.findAll().stream()
                .map(this::mapToRetrieveDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTOs.NotificationRetrieve> getById(@PathVariable String id) {
        return notificationService.findById(id)
                .map(notification -> ResponseEntity.ok(mapToRetrieveDTO(notification)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTOs.NotificationRetrieve> update(
            @PathVariable String id,
            @Valid @RequestBody NotificationDTOs.NotificationSubmit dto) {
        try {
            Notifications updated = notificationService.update(id, dto);
            return ResponseEntity.ok(mapToRetrieveDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private NotificationDTOs.NotificationRetrieve mapToRetrieveDTO(Notifications notification) {
        Date date = notification.getTimestamp() != null ? Date.from(notification.getTimestamp()) : null;
        return new NotificationDTOs.NotificationRetrieve(
                notification.getAuthor(),
                notification.getMessage(),
                date
        );
    }
}