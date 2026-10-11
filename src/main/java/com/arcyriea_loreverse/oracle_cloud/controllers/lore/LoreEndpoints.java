package com.arcyriea_loreverse.oracle_cloud.controllers.lore;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.CharacterDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.FactionDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.RaceDTOs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lore")
public class LoreEndpoints {
    FactionController factionController;
    RaceController raceController;
    CharacterController characterController;

    @GetMapping("/factions")
    public ResponseEntity<List<FactionDTOs.Retrieve>> getAllFactions(){
        return ResponseEntity.ok(factionController.factionService.findAll().stream().map(FactionController::mapToRetrieve).toList());
    }

    @GetMapping("/characters")
    public ResponseEntity<List<CharacterDTOs.Retrieve>> getAllCharacters(){
        return ResponseEntity.ok(characterController.characterService.findAll().stream().map(CharacterController::mapToRetrieve).toList());
    }

    @GetMapping("/races")
    public ResponseEntity<List<RaceDTOs.Retrieve>> getAllRaces(){
        return ResponseEntity.ok(raceController.raceService.findAll().stream().map(RaceController::mapToRetrieve).toList());
    }

}
