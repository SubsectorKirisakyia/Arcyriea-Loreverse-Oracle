package com.arcyriea_loreverse.oracle_cloud.crud.services.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.RaceDTOs.*;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Races;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.RaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RaceService {

    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @Transactional
    public Races create(Submit dto) {
        Races race = new Races();
        race.setName(dto.name());
        race.setLore(dto.lore());
        return raceRepository.save(race);
    }

    public List<Races> findAll() {
        return raceRepository.findAll();
    }

    public Races findById(Integer id) {
        return raceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Race not found with id: " + id));
    }

    @Transactional
    public Races update(Integer id, Submit dto) {
        Races race = findById(id);
        race.setName(dto.name());
        race.setLore(dto.lore());
        return raceRepository.save(race);
    }

    @Transactional
    public void delete(Integer id) {
        raceRepository.deleteById(id);
    }
}