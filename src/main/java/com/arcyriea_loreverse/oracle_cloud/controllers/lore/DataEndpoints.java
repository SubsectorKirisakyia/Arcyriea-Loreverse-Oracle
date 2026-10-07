package com.arcyriea_loreverse.oracle_cloud.controllers.lore;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.CharacterDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.FactionDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.RaceDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Characters;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Factions;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Races;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.CharacterService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.FactionService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class DataEndpoints {
    private final CharacterService characterService;
    private final FactionService factionService;
    private final RaceService raceService;

    @GetMapping("/factions")
    public ResponseEntity<List<FactionDTOs.Retrieve>> getAllFactions() {
        return ResponseEntity.ok(factionService.findAll().stream().map(FactionController::mapToRetrieve).toList());
    }

    @GetMapping("/factions/{id}")
    public ResponseEntity<FactionDTOs.Retrieve> getFactionById(@PathVariable Long id) {
        return ResponseEntity.ok(FactionController.mapToRetrieve(factionService.findById(id)));
    }

    @GetMapping("/characters")
    public ResponseEntity<List<CharacterDTOs.Retrieve>> getAllCharacters() {
        return ResponseEntity.ok(characterService.findAll().stream().map(CharacterController::mapToRetrieve).toList());
    }

    @GetMapping("/characters/{id}")
    public ResponseEntity<CharacterDTOs.Retrieve> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(CharacterController.mapToRetrieve(characterService.findById(id)));
    }

    @GetMapping("/races")
    public ResponseEntity<List<RaceDTOs.Retrieve>> getAllRaces() {
        return ResponseEntity.ok(raceService.findAll().stream().map(RaceController::mapToRetrieve).toList());
    }

    @GetMapping("/races/{id}")
    public ResponseEntity<RaceDTOs.Retrieve> getRaceById(@PathVariable Integer id) {
        return ResponseEntity.ok(RaceController.mapToRetrieve(raceService.findById(id)));
    }

}
