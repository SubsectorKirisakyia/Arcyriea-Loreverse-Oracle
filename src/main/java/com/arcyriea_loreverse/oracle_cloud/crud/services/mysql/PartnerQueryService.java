package com.arcyriea_loreverse.oracle_cloud.crud.services.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql.PartnerRepository;
import org.springframework.stereotype.Service;

@Service
public class PartnerQueryService {
    private final PartnerRepository repository;

    public PartnerQueryService(PartnerRepository repository) {
        this.repository = repository;
    }
}
