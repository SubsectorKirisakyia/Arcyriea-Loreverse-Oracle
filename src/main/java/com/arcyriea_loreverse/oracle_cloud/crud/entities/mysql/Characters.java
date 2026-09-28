package com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Characters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String maidenName;
    private String lastName;

    // Many Characters belong to One Faction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id") // Creates a 'faction_id' foreign key column in MySQL
    @ToString.Exclude // Prevents infinite loops when Lombok generates toString()
    private Factions faction;

    // Many Characters belong to One Race
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id") // Creates a 'race_id' foreign key column in MySQL
    @ToString.Exclude // Prevents infinite loops
    private Races race;
}
