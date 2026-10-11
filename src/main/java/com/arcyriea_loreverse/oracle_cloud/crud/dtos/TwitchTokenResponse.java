package com.arcyriea_loreverse.oracle_cloud.crud.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TwitchTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("expires_in")
        long expiresIn,

        @JsonProperty("token_type")
        String tokenType,

        List<String> scope
) {}
