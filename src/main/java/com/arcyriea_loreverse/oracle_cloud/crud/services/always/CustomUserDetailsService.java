package com.arcyriea_loreverse.oracle_cloud.crud.services.always;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Account;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.always.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the account in your "always" datasource
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + username));

        // 2. Map your Role enum to Spring Security's GrantedAuthority
        // Spring Security expects roles to start with "ROLE_" (e.g., ROLE_ADMIN)
        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        // 3. Return a UserDetails object
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPasswordHash(), // Note: using passwordHash from your Account entity
                account.isEnabled(), // Map your enabled boolean
                true, true, true, // Account non-expired, credentials non-expired, etc.
                authorities
        );
    }
}
