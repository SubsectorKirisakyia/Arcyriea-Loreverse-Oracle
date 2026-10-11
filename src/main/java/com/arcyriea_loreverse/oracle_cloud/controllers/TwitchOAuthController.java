package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.components.listerners.TwitchChatListener;
import com.arcyriea_loreverse.oracle_cloud.crud.services.TwitchTokenService;
import com.arcyriea_loreverse.oracle_cloud.properties.TwitchProperties;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/auth/twitch")
@RequiredArgsConstructor
public class TwitchOAuthController {

    private final TwitchProperties properties;
    private final TwitchTokenService tokenService;
    private final TwitchChatListener chatListener;

    @GetMapping
    public ResponseEntity<Void> authorize(
            HttpSession session) {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        String state = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        session.setAttribute("twitch_oauth_state", state);

        String url = UriComponentsBuilder
                .fromUriString(
                        "https://id.twitch.tv/oauth2/authorize")
                .queryParam("client_id", properties.clientId())
                .queryParam("redirect_uri", properties.redirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", "chat:read")
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session) {

        String expected = (String) session.getAttribute(
                "twitch_oauth_state");

        session.removeAttribute("twitch_oauth_state");

        if (expected == null || state == null
                || !MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                state.getBytes(StandardCharsets.UTF_8))) {
            return ResponseEntity.badRequest()
                    .body("Invalid OAuth state.");
        }

        if (error != null || code == null || code.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Twitch authorization was not completed.");
        }

        tokenService.exchangeCode(code);
        chatListener.connect();

        return ResponseEntity.ok(
                "Twitch authorized and chat connection started.");
    }
}
