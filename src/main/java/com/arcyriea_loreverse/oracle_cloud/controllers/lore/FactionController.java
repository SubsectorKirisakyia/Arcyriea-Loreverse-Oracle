package com.arcyriea_loreverse.oracle_cloud.controllers.lore;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.FactionDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Factions;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.FactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lore/factions")
public class FactionController {

    private final FactionService factionService;

    public FactionController(FactionService factionService) {
        this.factionService = factionService;
    }

    @PostMapping
    public ResponseEntity<FactionDTOs.Retrieve> create(@Valid @RequestBody FactionDTOs.Submit dto) {
        return new ResponseEntity<>(mapToRetrieve(factionService.create(dto)), HttpStatus.CREATED);
    }



    @PutMapping("/{id}")
    public ResponseEntity<FactionDTOs.Retrieve> update(@PathVariable Long id, @Valid @RequestBody FactionDTOs.Submit dto) {
        return ResponseEntity.ok(mapToRetrieve(factionService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        factionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    protected static FactionDTOs.Retrieve mapToRetrieve(Factions faction) {
        return new FactionDTOs.Retrieve(
                faction.getId(),
                faction.getName(),
                faction.getLore(),
                faction.getFoundingDate(),
                faction.getSymbol(),
                faction.getRace() != null ? faction.getRace().getId() : null,
                faction.getRace() != null ? faction.getRace().getName() : null
        );
    }
}