package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.crud.services.always.AuthService;
import lombok.RequiredArgsConstructor;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.always.AuthDTOs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}

