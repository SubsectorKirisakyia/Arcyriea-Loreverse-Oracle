package com.arcyriea_loreverse.oracle_cloud.configs.securities;

import com.arcyriea_loreverse.oracle_cloud.configs.filters.JwtAuthenticationFilter;
import com.arcyriea_loreverse.oracle_cloud.configs.filters.RateLimitFilter;
import io.sentry.Sentry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "spring.security.enabled", havingValue="true")
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private JwtUtils utils;

    @Autowired
    private SpringLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Public login/register endpoints
                        .requestMatchers("/api/public/**").permitAll() // Any public lore endpoints
                        .requestMatchers("/api/accounts/**").hasRole("ADMIN")
                        .requestMatchers("/api/lore/**").hasAnyRole("ADMIN", "PRIVATE")
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/notif/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").hasAnyRole("ADMIN", "PRIVATE")
                        .requestMatchers("/actuator/**").hasAnyRole("ADMIN", "PRIVATE")
                        .requestMatchers("/login", "/logout").permitAll()
                        .anyRequest().authenticated() // Everything else is locked
                )
                .formLogin(form -> form
                        .successHandler((request, response, authentication) -> {
                            String jwtToken = utils.generateToken(authentication.getName());

                            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                                    .httpOnly(true)
                                    .secure(true) // Set to true in production (HTTPS)
                                    .path("/")
                                    .maxAge(86400) // 24 hours expiry
                                    .build();

                            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

                            boolean hasAccess = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") ||
                                            auth.getAuthority().equals("ROLE_PRIVATE"));

                            if (hasAccess){
                                response.sendRedirect("/swagger-ui/index.html");
                            } else {
                                Sentry.logger().warn("User " + authentication.getName() + " tries to login to our unauthorized system endpoint.");
                                response.sendRedirect("/logout");
                            }
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .deleteCookies("jwt")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.sendRedirect("/login");
                        })
                );

        // Add our JWT filter before the standard Spring Security filter
        http.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

