package com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PartnerDTOs {
    public record Submit(
        @NotBlank @Size(max = 25) String userName,
        String avatarUrl,
        @Size(max = 25) String youtubeName,
        String youtubeUrl,
        String youtubeAvatar,
        @Size(max = 25) String twitchName,
        String twitchUrl,
        String twitchAvatar,
        String description,
        Gender gender,
        Relationship relation,
        Affiliation status,
        BrandCompany tuberBrand
    ) {}

    public record Retrieve(
        Long id,
        String userName,
        String avatarUrl,
        String youtubeName,
        String youtubeUrl,
        String youtubeAvatar,
        String twitchName,
        String twitchUrl,
        String twitchAvatar,
        String description,
        Gender gender,
        Relationship relation,
        Affiliation status,
        BrandCompany tuberBrand
    ) {}
}