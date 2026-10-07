package com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Factions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String lore;
    private LocalDate foundingDate;

    private String symbol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Races race;

    @OneToMany(mappedBy = "faction", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Characters> characters;
}
