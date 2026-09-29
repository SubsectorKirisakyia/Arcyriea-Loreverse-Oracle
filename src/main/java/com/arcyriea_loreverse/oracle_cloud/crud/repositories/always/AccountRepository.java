package com.arcyriea_loreverse.oracle_cloud.crud.repositories.always;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}