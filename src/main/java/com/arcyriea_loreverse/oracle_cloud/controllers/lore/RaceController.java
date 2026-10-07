package com.arcyriea_loreverse.oracle_cloud.controllers.lore;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.RaceDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Races;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.RaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lore/races")
public class RaceController {

    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @PostMapping
    public ResponseEntity<RaceDTOs.Retrieve> create(@Valid @RequestBody RaceDTOs.Submit dto) {
        return new ResponseEntity<>(mapToRetrieve(raceService.create(dto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaceDTOs.Retrieve> update(@PathVariable Integer id, @Valid @RequestBody RaceDTOs.Submit dto) {
        return ResponseEntity.ok(mapToRetrieve(raceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        raceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    protected static RaceDTOs.Retrieve mapToRetrieve(Races race) {
        return new RaceDTOs.Retrieve(race.getId(), race.getName(), race.getLore());
    }
}