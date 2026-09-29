package com.arcyriea_loreverse.oracle_cloud.configs;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// Simple in-memory rate limiter (for a single instance)
@Configuration
public class RateLimitConfig {

    @Bean
    public Bucket bucket() {
        // Allow 100 requests per minute per IP
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
}
