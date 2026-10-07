package com.arcyriea_loreverse.oracle_cloud.controllers.system;

import com.arcyriea_loreverse.oracle_cloud.crud.services.always.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.arcyriea_loreverse.oracle_cloud.crud.dtos.always.AuthDTOs.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.security.enabled", havingValue="true")
public class AuthController {

    private final AuthService authService;
    private final Environment env;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResult result = authService.authenticate(request);

        if (result != null){
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", result.token())
                    .httpOnly(true)
                    .secure(isSecure())
                    .path("/")
                    .maxAge(86400)
                    .sameSite("None")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(result.response());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(isSecure())
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    private boolean isSecure(){
        return env.getProperty("server.ssl.enabled", Boolean.class, false);
    }
}

