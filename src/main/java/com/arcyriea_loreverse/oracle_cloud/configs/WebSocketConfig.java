package com.arcyriea_loreverse.oracle_cloud.configs;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
public class WebSocketConfig{

    @Configuration
    @EnableWebSocketMessageBroker
    public static class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

        @Value("${app.cors.allowed-origins}")
        private String allowedOrigins;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
            stompEndpointRegistry.addEndpoint("/stream")
                    .setAllowedOrigins(allowedOrigins.split(","))
                    .withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker("/topic");
            registry.setApplicationDestinationPrefixes("/app");
        }
    }
}