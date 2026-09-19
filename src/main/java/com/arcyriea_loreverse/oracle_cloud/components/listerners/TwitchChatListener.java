package com.arcyriea_loreverse.oracle_cloud.components.listerners;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;

@ConditionalOnProperty(name = "listener.twitch.enabled", havingValue="true")
@Component
public class TwitchChatListener {

    private final UnifiedChatService service;
    private TwitchClient twitchClient;

    @Value("${twitch.oauth-token}")
    private String oauthToken;

    @Value("${twitch.channel}")
    private String channel;

    public TwitchChatListener(UnifiedChatService service) {
        this.service = service;
    }

    @PostConstruct
    public void start() {
        OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);

        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        SimpleEventHandler handler = twitchClient.getEventManager()
                .getEventHandler(SimpleEventHandler.class);

        // Core: save every chat message
        handler.onEvent(ChannelMessageEvent.class, event -> {
            Chats chat = new Chats();
            chat.setUsername(event.getUser().getName());
            chat.setSource("twitch");
            chat.setMessage(event.getMessage());
            chat.setTimestamp(Instant.now());
            service.save(chat);
        });

        // Optional: log when stream goes live/offline
        handler.onEvent(ChannelGoLiveEvent.class, e ->
                System.out.println("[Twitch] " + e.getChannel().getName() + " went LIVE"));
        handler.onEvent(ChannelGoOfflineEvent.class, e ->
                System.out.println("[Twitch] " + e.getChannel().getName() + " went OFFLINE"));

        twitchClient.getChat().joinChannel(channel);
        System.out.println("[Twitch] Joined #" + channel);
    }

    @PreDestroy
    public void stop() {
        if (twitchClient != null) twitchClient.close();
    }
}
