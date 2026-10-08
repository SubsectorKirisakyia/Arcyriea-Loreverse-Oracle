package com.arcyriea_loreverse.oracle_cloud.crud.services.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.mysql.PartnerDTOs.*;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Partners;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerQueryService {

    private final PartnerRepository partnerRepository;

    @Transactional
    public Partners create(Submit dto) {
        Partners partner = new Partners();
        mapFields(partner, dto);
        return partnerRepository.save(partner);
    }

    public List<Partners> findAll() {
        return partnerRepository.findAll();
    }

    public Partners findById(Long id) {
        return partnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found with id: " + id));
    }

    @Transactional
    public Partners update(Long id, Submit dto) {
        Partners partner = findById(id);
        mapFields(partner, dto);
        return partnerRepository.save(partner);
    }

    @Transactional
    public void delete(Long id) {
        partnerRepository.deleteById(id);
    }

    private void mapFields(Partners partner, Submit dto) {
        partner.setUserName(dto.userName());
        partner.setAvatarUrl(dto.avatarUrl());
        partner.setYoutubeName(dto.youtubeName());
        partner.setYoutubeUrl(dto.youtubeUrl());
        partner.setYoutubeAvatar(dto.youtubeAvatar());
        partner.setTwitchName(dto.twitchName());
        partner.setTwitchUrl(dto.twitchUrl());
        partner.setTwitchAvatar(dto.twitchAvatar());
        partner.setDescription(dto.description());
        partner.setGender(dto.gender());
        partner.setRelation(dto.relation());
        partner.setStatus(dto.status());
        partner.setTuberBrand(dto.tuberBrand());
    }
}