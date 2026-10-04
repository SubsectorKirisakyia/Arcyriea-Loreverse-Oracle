package com.arcyriea_loreverse.oracle_cloud.crud.services.always;

import com.arcyriea_loreverse.oracle_cloud.configs.securities.JwtUtils;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.always.AuthDTOs.*;
import com.arcyriea_loreverse.oracle_cloud.crud.entities.always.Account;
import com.arcyriea_loreverse.oracle_cloud.crud.repositories.always.AccountRepository;
import com.google.auth.oauth2.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtProvider;
    private final AccountRepository accountRepository;

    public AuthResponse authenticate(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        if (!authentication.isAuthenticated()) throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Incorrect Username or Password"
        );;

        String username = authentication.getName();
        String token = jwtProvider.generateToken(username);

        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, account.getUsername(), account.getRoles());
    }
}

