package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.crud.services.always.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResult result = authService.authenticate(request);

        Cookie jwtCookie = new Cookie("jwt", result.token());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 24 hours
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(result.response());
    }
}

