package com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CharacterDTOs {
    public record Submit(
        @NotBlank String firstName,
        String maidenName,
        String lastName,
        String nameOrder,
        String portrait,
        List<String> references,
        Long factionId,
        Integer raceId
    ) {}

    public record Retrieve(
        Long id,
        String firstName,
        String maidenName,
        String lastName,
        String fullName,
        String nameOrder,
        String portrait,
        List<String> references,
        Long factionId,
        String factionName,
        Integer raceId,
        String raceName
    ) {}
}