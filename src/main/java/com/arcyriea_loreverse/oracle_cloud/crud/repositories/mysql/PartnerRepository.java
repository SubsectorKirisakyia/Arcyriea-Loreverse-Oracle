package com.arcyriea_loreverse.oracle_cloud.crud.repositories.mysql;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mysql.Partners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends JpaRepository<Partners, Long> {

}
