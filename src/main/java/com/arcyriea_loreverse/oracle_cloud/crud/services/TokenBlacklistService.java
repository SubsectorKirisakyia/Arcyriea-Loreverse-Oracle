package com.arcyriea_loreverse.oracle_cloud.crud.services;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void blacklistToken(String token) {
        if (!token.isEmpty()) blacklistedTokens.add(token);
    }
}
