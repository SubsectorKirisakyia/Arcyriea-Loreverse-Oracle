package com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.enums.Affiliation;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.enums.BrandCompany;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.enums.Gender;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.enums.Relationship;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="Partner")
public class Partners {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="USERNAME", length = 25, nullable=false, unique=true)
    private String userName;

    @Column(name="AVATAR")
    private String avatarUrl;

    @Column(name="YOUTUBE_CHANNEL", length = 25, unique=true)
    private String youtubeName;
    @Column(name="YOUTUBE_URL", unique=true)
    private String youtubeUrl;
    @Column(name="YOUTUBE_AVATAR")
    private String youtubeAvatar;

    @Column(name="TWITCH_CHANNEL", length = 25, unique=true)
    private String twitchName;
    @Column(name="TWITCH_URL", unique=true)
    private String twitchUrl;
    @Column(name="TWITCH_AVATAR")
    private String twitchAvatar;

    @Column(name="DESCRIPTION")
    private String description;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Relationship relation;
    @Enumerated(EnumType.STRING)
    private Affiliation status;

    @Column(name="VTUBER_BRAND")
    private BrandCompany tuberBrand;
}
