package com.arcyriea_loreverse.oracle_cloud.crud.services.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.FactionDTOs.*;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Factions;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Races;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.FactionRepository;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.RaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FactionService {

    private final FactionRepository factionRepository;
    private final RaceRepository raceRepository;

    public FactionService(FactionRepository factionRepository, RaceRepository raceRepository) {
        this.factionRepository = factionRepository;
        this.raceRepository = raceRepository;
    }

    @Transactional
    public Factions create(Submit dto) {
        Factions faction = new Factions();
        faction.setName(dto.name());
        faction.setLore(dto.lore());
        faction.setFoundingDate(dto.foundingDate());
        faction.setSymbol(dto.symbol());

        if (dto.raceId() != null) {
            Races race = raceRepository.findById(dto.raceId())
                    .orElseThrow(() -> new RuntimeException("Race not found with id: " + dto.raceId()));
            faction.setRace(race);
        }

        return factionRepository.save(faction);
    }

    public List<Factions> findAll() {
        return factionRepository.findAll();
    }

    public Factions findById(Long id) {
        return factionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faction not found with id: " + id));
    }

    @Transactional
    public Factions update(Long id, Submit dto) {
        Factions faction = findById(id);
        faction.setName(dto.name());
        faction.setLore(dto.lore());
        faction.setFoundingDate(dto.foundingDate());
        faction.setSymbol(dto.symbol());

        if (dto.raceId() != null) {
            Races race = raceRepository.findById(dto.raceId())
                    .orElseThrow(() -> new RuntimeException("Race not found with id: " + dto.raceId()));
            faction.setRace(race);
        } else {
            faction.setRace(null);
        }

        return factionRepository.save(faction);
    }

    @Transactional
    public void delete(Long id) {
        factionRepository.deleteById(id);
    }
}