package com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql;

import jakarta.validation.constraints.NotBlank;

public class RaceDTOs {
    public record Submit(
        @NotBlank String name,
        String lore
    ) {}

    public record Retrieve(
        Integer id,
        String name,
        String lore
    ) {}
}