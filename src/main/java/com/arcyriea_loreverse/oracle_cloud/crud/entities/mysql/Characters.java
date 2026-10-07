package com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

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

    private String nameOrder;

    private String portrait;

    @ElementCollection
    @CollectionTable(name = "character_references", joinColumns = @JoinColumn(name = "character_id"))
    @Column(name = "reference_url")
    private List<String> references;

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

    public String getFullName(String ordering) {
        String first = (firstName != null && !firstName.trim().isEmpty()) ? firstName + " " : "";
        String maiden = (maidenName != null && !maidenName.trim().isEmpty()) ? maidenName + " " : "";
        String last = (lastName != null && !lastName.trim().isEmpty()) ? lastName + " " : "";

        String fullName = switch (ordering) {
            case "MFL" -> maiden + first + last;
            case "FLM" -> first + last + maiden;
            default -> first + maiden + last;
        };

        return fullName.trim();
    }
}
