package com.arcyriea_loreverse.oracle_cloud.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twitch")
public record TwitchProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String channel
) {}
