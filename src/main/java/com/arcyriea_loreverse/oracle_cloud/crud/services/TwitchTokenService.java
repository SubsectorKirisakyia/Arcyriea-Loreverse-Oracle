package com.arcyriea_loreverse.oracle_cloud.crud.services;

import com.arcyriea_loreverse.oracle_cloud.crud.dtos.TwitchTokenResponse;
import com.arcyriea_loreverse.oracle_cloud.properties.TwitchProperties;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
@ConditionalOnProperty(
        prefix = "twitch",
        name = {"channel", "client-id", "client-secret", "redirect-uri"}
)
public class TwitchTokenService {

    private final RestClient restClient;
    private final TwitchProperties properties;

    @Getter
    private volatile String accessToken;
    @Getter
    private volatile String refreshToken;
    @Getter
    private volatile Instant expiresAt;

    public TwitchTokenService(TwitchProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.create();
    }

    public synchronized void exchangeCode(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", properties.redirectUri());

        TwitchTokenResponse response = restClient.post()
                .uri("https://id.twitch.tv/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TwitchTokenResponse.class);

        if (response == null || response.accessToken() == null || response.refreshToken() == null) {
            throw new IllegalStateException("Twitch did not return both tokens");
        }

        updateTokens(response);
    }

    /**
     * Refresh the access token using the stored refresh token.
     * Returns true if refresh succeeded.
     */
    public synchronized boolean refreshAccessToken() {
        if (refreshToken == null || refreshToken.isBlank()) {
            return false;
        }

        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("client_id", properties.clientId());
            form.add("client_secret", properties.clientSecret());
            form.add("grant_type", "refresh_token");
            form.add("refresh_token", refreshToken);

            TwitchTokenResponse response = restClient.post()
                    .uri("https://id.twitch.tv/oauth2/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TwitchTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                return false;
            }

            // Twitch may or may not return a new refresh token
            if (response.refreshToken() != null) {
                this.refreshToken = response.refreshToken();
            }

            this.accessToken = response.accessToken();
            this.expiresAt = Instant.now().plusSeconds(response.expiresIn());

            System.out.println("[Twitch] Access token refreshed successfully.");
            return true;

        } catch (Exception e) {
            System.err.println("[Twitch] Failed to refresh token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns a valid access token. Refreshes automatically if expired or about to expire.
     */
    public synchronized String getValidAccessToken() {
        // Refresh 2 minutes before expiry
        if (accessToken == null || expiresAt == null || Instant.now().isAfter(expiresAt.minusSeconds(120))) {
            if (!refreshAccessToken()) {
                throw new IllegalStateException("No valid Twitch token available. Please re-authorize.");
            }
        }
        return accessToken;
    }

    private void updateTokens(TwitchTokenResponse response) {
        this.accessToken = response.accessToken();
        this.refreshToken = response.refreshToken();
        this.expiresAt = Instant.now().plusSeconds(response.expiresIn());
    }

    public boolean hasTokens() {
        return accessToken != null && refreshToken != null;
    }
}