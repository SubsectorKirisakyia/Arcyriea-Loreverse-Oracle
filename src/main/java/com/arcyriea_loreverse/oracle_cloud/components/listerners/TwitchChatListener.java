package com.arcyriea_loreverse.oracle_cloud.components.listerners;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.TwitchTokenService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;
import com.arcyriea_loreverse.oracle_cloud.properties.TwitchProperties;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;


@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "twitch",
    name = {"channel", "client-id", "client-secret", "redirect-uri"}
)
public class TwitchChatListener {

    private final UnifiedChatService service;
    private TwitchClient twitchClient;

    private final TwitchTokenService tokenService;
    private final TwitchProperties properties;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public synchronized void connect() {
        if (!tokenService.hasTokens()) {
            throw new IllegalStateException(
                    "Authorize Twitch before connecting.");
        }

        // Avoid duplicate clients if authorization runs again.
        if (twitchClient != null) {
            twitchClient.close();
            twitchClient = null;
        }

        OAuth2Credential credential =
                new OAuth2Credential(
                        "twitch",
                        tokenService.getAccessToken());

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        // Register your existing handlers here:
        SimpleEventHandler handler = twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class);

        // Core: save every chat message
        handler.onEvent(ChannelMessageEvent.class, event -> {
            Chats chat = new Chats();
            chat.setUsername(event.getUser().getName());
            chat.setSource("twitch");
            chat.setMessage(event.getMessage());
            chat.setTimestamp(Instant.now());
            messagingTemplate.convertAndSend("/topic/chats", service.save(chat));
        });

        // Optional: log when stream goes live/offline
        handler.onEvent(ChannelGoLiveEvent.class, e ->
                System.out.println("[Twitch] " + e.getChannel().getName() + " went LIVE"));
        handler.onEvent(ChannelGoOfflineEvent.class, e ->
                System.out.println("[Twitch] " + e.getChannel().getName() + " went OFFLINE"));

        twitchClient.getChat().joinChannel(
                properties.channel());

        System.out.println("[Twitch] Connected to chat.");
    }

    @PreDestroy
    public void stop() {
        if (twitchClient != null) twitchClient.close();
    }
}
