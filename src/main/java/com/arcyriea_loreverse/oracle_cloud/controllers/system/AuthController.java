package com.arcyriea_loreverse.oracle_cloud.controllers.system;

import com.arcyriea_loreverse.oracle_cloud.configs.securities.JwtUtils;
import com.arcyriea_loreverse.oracle_cloud.crud.services.TokenBlacklistService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.always.AuthService;
import io.sentry.Sentry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.util.WebUtils;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.security.enabled", havingValue="true")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService blacklistService;
    private final JwtUtils utils;
    private final Environment env;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest login, HttpServletRequest request) {
        try {
            AuthResult result = authService.authenticate(login);

            if (result != null){
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", result.token())
                        .httpOnly(true)
                        .secure(request.isSecure())
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
        } catch (Exception e) {
            Sentry.logger().error("Unhandled login exception: "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            Cookie cookie = WebUtils.getCookie(request, "jwt");
            if (cookie == null) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

            SecurityContextHolder.clearContext();
            String token = cookie.getValue();

            var expirationTime = (utils.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000;

            blacklistService.blacklistToken(token, "blacklisted", expirationTime, TimeUnit.SECONDS);

            ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(request.isSecure())
                    .path("/")
                    .maxAge(0)
                    .sameSite("None")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .build();
        } catch (Exception e){
            Sentry.logger().error("Unhandled logout exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}

