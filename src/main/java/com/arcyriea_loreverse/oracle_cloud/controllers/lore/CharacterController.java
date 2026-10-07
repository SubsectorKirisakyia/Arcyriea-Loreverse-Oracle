package com.arcyriea_loreverse.oracle_cloud.controllers.lore;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.CharacterDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Characters;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.CharacterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lore/characters")
public class CharacterController {

    private final CharacterService characterService;

    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @PostMapping
    public ResponseEntity<CharacterDTOs.Retrieve> create(@Valid @RequestBody CharacterDTOs.Submit dto) {
        return new ResponseEntity<>(mapToRetrieve(characterService.create(dto)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CharacterDTOs.Retrieve> update(@PathVariable Long id, @Valid @RequestBody CharacterDTOs.Submit dto) {
        return ResponseEntity.ok(mapToRetrieve(characterService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        characterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    protected static CharacterDTOs.Retrieve mapToRetrieve(Characters character) {
        String nameOrder = character.getNameOrder() != null ? character.getNameOrder() : "DEFAULT";
        return new CharacterDTOs.Retrieve(
                character.getId(),
                character.getFirstName(),
                character.getMaidenName(),
                character.getLastName(),
                character.getFullName(nameOrder),
                character.getNameOrder(),
                character.getPortrait(),
                character.getReferences(),
                character.getFaction() != null ? character.getFaction().getId() : null,
                character.getFaction() != null ? character.getFaction().getName() : null,
                character.getRace() != null ? character.getRace().getId() : null,
                character.getRace() != null ? character.getRace().getName() : null
        );
    }
}