package com.arcyriea_loreverse.oracle_cloud.crud.services.always;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Account;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.always.AccountRepository;
import lombok.RequiredArgsConstructor;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.always.AccountDTOs.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor // Lombok
@ConditionalOnProperty(name = "spring.security.enabled", havingValue="true")
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder; // Inject BCryptPasswordEncoder

    public AccountResponse createAccount(AccountRequest request) {
        if (accountRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        Account account = new Account();
        account.setUsername(request.username());
        // HASH THE PASSWORD before saving
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRoles(request.roles());
        account.setEnabled(true);

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public AccountResponse getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        account.setUsername(request.username());
        if (request.password() != null && !request.password().isBlank()) {
            account.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        account.setRoles(request.roles());

        return mapToResponse(accountRepository.save(account));
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        accountRepository.deleteById(id);
    }

    // Helper method to convert Entity -> DTO
    private AccountResponse mapToResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.isEnabled(),
                account.getRoles()
        );
    }
}

