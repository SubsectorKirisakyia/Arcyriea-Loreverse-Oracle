package com.arcyriea_loreverse.oracle_cloud.crud.services.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.CharacterDTOs.*;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Characters;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Factions;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Races;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.CharacterRepository;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.FactionRepository;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.RaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final FactionRepository factionRepository;
    private final RaceRepository raceRepository;

    public CharacterService(CharacterRepository characterRepository, FactionRepository factionRepository, RaceRepository raceRepository) {
        this.characterRepository = characterRepository;
        this.factionRepository = factionRepository;
        this.raceRepository = raceRepository;
    }

    @Transactional
    public Characters create(Submit dto) {
        Characters character = new Characters();
        mapFields(character, dto);
        return characterRepository.save(character);
    }

    public List<Characters> findAll() {
        return characterRepository.findAll();
    }

    public Characters findById(Long id) {
        return characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));
    }

    @Transactional
    public Characters update(Long id, Submit dto) {
        Characters character = findById(id);
        mapFields(character, dto);
        return characterRepository.save(character);
    }

    @Transactional
    public void delete(Long id) {
        characterRepository.deleteById(id);
    }

    private void mapFields(Characters character, Submit dto) {
        character.setFirstName(dto.firstName());
        character.setMaidenName(dto.maidenName());
        character.setLastName(dto.lastName());
        character.setNameOrder(dto.nameOrder());
        character.setPortrait(dto.portrait());
        character.setReferences(dto.references());

        if (dto.factionId() != null) {
            Factions faction = factionRepository.findById(dto.factionId())
                    .orElseThrow(() -> new RuntimeException("Faction not found with id: " + dto.factionId()));
            character.setFaction(faction);
        } else {
            character.setFaction(null);
        }

        if (dto.raceId() != null) {
            Races race = raceRepository.findById(dto.raceId())
                    .orElseThrow(() -> new RuntimeException("Race not found with id: " + dto.raceId()));
            character.setRace(race);
        } else {
            character.setRace(null);
        }
    }
}