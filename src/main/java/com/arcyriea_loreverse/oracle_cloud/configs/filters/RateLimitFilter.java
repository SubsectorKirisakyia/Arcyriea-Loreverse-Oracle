package com.arcyriea_loreverse.oracle_cloud.configs.filters;

import com.arcyriea_loreverse.oracle_cloud.crud.services.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get the client IP address
        String clientIp = getClientIp(request);

        // 2. Get the bucket for this IP
        var bucket = rateLimitService.resolveBucket(clientIp);

        // 3. Try to consume 1 token
        if (bucket.tryConsume(1)) {
            // Token available, proceed to the next filter/controller
            filterChain.doFilter(request, response);
        } else {
            // No tokens left! Return HTTP 429 Too Many Requests
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again in a minute.\"}");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        // If your app is behind a proxy (like Nginx or Cloudflare),
        // the real IP is in the 'X-Forwarded-For' header.
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        // Otherwise, use the direct remote address
        return request.getRemoteAddr();
    }
}

