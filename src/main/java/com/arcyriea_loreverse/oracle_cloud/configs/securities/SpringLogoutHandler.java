package com.arcyriea_loreverse.oracle_cloud.configs.securities;

import com.arcyriea_loreverse.oracle_cloud.crud.services.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // Reserved when needed.
    }

}
