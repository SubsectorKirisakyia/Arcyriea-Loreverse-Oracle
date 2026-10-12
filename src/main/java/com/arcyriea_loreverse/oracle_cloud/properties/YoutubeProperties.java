package com.arcyriea_loreverse.oracle_cloud.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youtube")
public record YoutubeProperties(
        String channelId,
        Api api
) {
    public record Api(String key) {}
}
