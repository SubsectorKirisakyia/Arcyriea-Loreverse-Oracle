package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.PartnerDTOs;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Partners;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mysql.PartnerQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerQueryService partnerQueryService;

    public PartnerController(PartnerQueryService partnerQueryService) {
        this.partnerQueryService = partnerQueryService;
    }

    @PostMapping
    public ResponseEntity<PartnerDTOs.Retrieve> create(@Valid @RequestBody PartnerDTOs.Submit dto) {
        return new ResponseEntity<>(mapToRetrieve(partnerQueryService.create(dto)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PartnerDTOs.Retrieve>> getAll() {
        return ResponseEntity.ok(partnerQueryService.findAll().stream().map(this::mapToRetrieve).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDTOs.Retrieve> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToRetrieve(partnerQueryService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerDTOs.Retrieve> update(@PathVariable Long id, @Valid @RequestBody PartnerDTOs.Submit dto) {
        return ResponseEntity.ok(mapToRetrieve(partnerQueryService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partnerQueryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PartnerDTOs.Retrieve mapToRetrieve(Partners partner) {
        return new PartnerDTOs.Retrieve(
                partner.getId(),
                partner.getUserName(),
                partner.getAvatarUrl(),
                partner.getYoutubeName(),
                partner.getYoutubeUrl(),
                partner.getYoutubeAvatar(),
                partner.getTwitchName(),
                partner.getTwitchUrl(),
                partner.getTwitchAvatar(),
                partner.getDescription(),
                partner.getGender(),
                partner.getRelation(),
                partner.getStatus(),
                partner.getTuberBrand()
        );
    }
}