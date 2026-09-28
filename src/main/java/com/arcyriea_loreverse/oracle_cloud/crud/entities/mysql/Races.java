package com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Races")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Races {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String lore;
    // One Race can have Many Factions
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Factions> subfactions;

    // One Race can have Many Characters
    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    @ToString.Exclude // Prevents infinite recursion
    @EqualsAndHashCode.Exclude // Prevents infinite recursion
    private List<Characters> characters;
}
