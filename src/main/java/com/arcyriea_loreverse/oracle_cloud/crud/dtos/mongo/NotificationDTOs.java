package com.arcyriea_loreverse.oracle_cloud.crud.dtos.mongo;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public class NotificationDTOs {
    public record NotificationSubmit(
        String author,
        @NotBlank String message
    ) {}

    public record NotificationRetrieve(
        String author,
        String message,
        Date date
    ) {}
}
