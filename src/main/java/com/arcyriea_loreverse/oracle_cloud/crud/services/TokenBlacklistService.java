package com.arcyriea_loreverse.oracle_cloud.crud.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final StringRedisTemplate redis;

    public boolean isBlacklisted(String token) {
        try {
            return redis.hasKey(token);
        } catch (Exception e) {
            log.error("Failed to check token in Redis: {}", e.getMessage());
            return false;
        }
    }

    public void blacklistToken(String token, String value, long ttl, TimeUnit unit) {
        if (!token.isEmpty()) {
            try {
                redis.opsForValue().set(token, value, ttl, unit);
                log.info("Token blacklisted in Redis: {}", token.substring(0, 20) + "...");
            } catch (Exception e) {
                log.error("Failed to blacklist token in Redis: {}", e.getMessage());
            }
        }
    }
}
