package com.arcyriea_loreverse.oracle_cloud.configs.securities;

import com.arcyriea_loreverse.oracle_cloud.crud.services.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SpringLogoutHandler implements LogoutHandler {

    private final TokenBlacklistService blacklistService;
    private final JwtUtils utils;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (request.getCookies() != null) {
            // Find your custom JWT cookie inside the array
            Arrays.stream(request.getCookies())
                    .filter(cookie -> "jwt".equals(cookie.getName()))
                    .findFirst()
                    .ifPresent(cookie -> {
                        String token = cookie.getValue();

                        var expirationTime = (utils.extractExpiration(token).getTime() - System.currentTimeMillis()) / 1000;

                        blacklistService.blacklistToken(token, "blacklisted", expirationTime, TimeUnit.SECONDS);
                    });
        }
    }

}
