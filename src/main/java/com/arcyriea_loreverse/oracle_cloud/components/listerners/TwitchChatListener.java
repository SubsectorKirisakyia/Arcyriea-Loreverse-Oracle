package com.arcyriea_loreverse.oracle_cloud.components.listerners;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.TwitchTokenService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;
import com.arcyriea_loreverse.oracle_cloud.properties.TwitchProperties;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.ChatConnectionStateEvent;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.client.websocket.domain.WebsocketConnectionState;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "twitch",
        name = {"channel", "client-id", "client-secret", "redirect-uri"}
)
public class TwitchChatListener {

    private final UnifiedChatService service;
    private final TwitchTokenService tokenService;
    private final TwitchProperties properties;
    private final SimpMessagingTemplate messagingTemplate;

    private TwitchClient twitchClient;
    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private final ScheduledExecutorService reconnectExecutor =
            Executors.newSingleThreadScheduledExecutor();

    public synchronized void connect() {
        if (connecting.get()) {
            return;
        }

        connecting.set(true);

        try {
            if (!tokenService.hasTokens()) {
                throw new IllegalStateException("Authorize Twitch before connecting.");
            }

            // Close existing client if any
            if (twitchClient != null) {
                try {
                    twitchClient.close();
                } catch (Exception ignored) {}
                twitchClient = null;
            }

            // Get a valid (possibly refreshed) token
            String accessToken = tokenService.getValidAccessToken();

            OAuth2Credential credential = new OAuth2Credential("twitch", accessToken);

            twitchClient = TwitchClientBuilder.builder()
                    .withEnableChat(true)
                    .withChatAccount(credential)
                    .build();

            SimpleEventHandler handler = twitchClient.getEventManager()
                    .getEventHandler(SimpleEventHandler.class);

            // Chat messages
            handler.onEvent(ChannelMessageEvent.class, event -> {
                Chats chat = new Chats();
                chat.setUsername(event.getUser().getName());
                chat.setSource("twitch");
                chat.setMessage(event.getMessage());
                chat.setTimestamp(Instant.now());
                messagingTemplate.convertAndSend("/topic/chats", service.save(chat));
            });

            // Optional live/offline logs
            handler.onEvent(ChannelGoLiveEvent.class, e ->
                    System.out.println("[Twitch] " + e.getChannel().getName() + " went LIVE"));
            handler.onEvent(ChannelGoOfflineEvent.class, e ->
                    System.out.println("[Twitch] " + e.getChannel().getName() + " went OFFLINE"));

            // Detect connection state changes
            handler.onEvent(ChatConnectionStateEvent.class, event -> {
                WebsocketConnectionState state = event.getState();

                if (state == WebsocketConnectionState.DISCONNECTED ||
                        state == WebsocketConnectionState.LOST) {

                    System.err.println("[Twitch] Chat connection lost (" + state + "). Reconnecting in 10s...");
                    scheduleReconnect(10);
                }
            });

            twitchClient.getChat().joinChannel(properties.channel());

            System.out.println("[Twitch] Connected to chat: " + properties.channel());

        } catch (Exception e) {
            System.err.println("[Twitch] Failed to connect: " + e.getMessage());
            scheduleReconnect(15);
        } finally {
            connecting.set(false);
        }
    }

    private void scheduleReconnect(int delaySeconds) {
        if (shuttingDown.get() || reconnectExecutor.isShutdown()) {
            return; // do not schedule during shutdown
        }

        reconnectExecutor.schedule(() -> {
            if (!shuttingDown.get()) {
                System.out.println("[Twitch] Attempting reconnect...");
                connect();
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        shuttingDown.set(true);
        reconnectExecutor.shutdownNow();

        if (twitchClient != null) {
            try {
                twitchClient.close();
            } catch (Exception ignored) {}
        }
    }
}