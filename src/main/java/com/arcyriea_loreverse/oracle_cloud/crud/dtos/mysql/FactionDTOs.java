package com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class FactionDTOs {
    public record Submit(
        @NotBlank String name,
        String lore,
        LocalDate foundingDate,
        String symbol,
        Integer raceId
    ) {}

    public record Retrieve(
        Long id,
        String name,
        String lore,
        LocalDate foundingDate,
        String symbol,
        Integer raceId,
        String raceName
    ) {}
}