package com.arcyriea_loreverse.oracle_cloud.components.listerners;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.model.LiveChatMessage;
import com.google.api.services.youtube.model.LiveChatMessageListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.google.api.services.youtube.YouTube;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(name = "listener.youtube.enabled", havingValue="true")
@Component
public class YoutubeChatListener {

    private final UnifiedChatService service;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;
    private volatile boolean running = true;
    private String nextPageToken = null;

    @Value("${youtube.live-chat-id}")
    private String liveChatId;

    @Value("${youtube.api.key}")
    private String apiKey; // Simple & robust for public read-only chat streams

    public YoutubeChatListener(UnifiedChatService service) {
        this.service = service;
    }

    @PostConstruct
    public void start() {
        if (liveChatId == null || liveChatId.isBlank()) {
            return;
        }
        // Kick off the initial poll immediately
        scheduledTask = scheduler.schedule(this::pollAndReschedule, 0, TimeUnit.MILLISECONDS);
    }

    private void pollAndReschedule() {
        if (!running) return;

        long nextDelay = 5000; // fallback default
        try {
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    null) // Pass null if using a simple API key query parameter
                    .setApplicationName("chat-archiver")
                    .build();

            LiveChatMessageListResponse response = youtube
                    .liveChatMessages()
                    .list(liveChatId, List.of("snippet", "authorDetails"))
                    .setKey(apiKey)
                    .setPageToken(nextPageToken)
                    .setFields("items(authorDetails(displayName),snippet(type,textMessageDetails(messageText))),nextPageToken,pollingIntervalMillis")
                    .execute();

            nextPageToken = response.getNextPageToken();

            List<LiveChatMessage> items = response.getItems();
            if (items != null) {
                for (LiveChatMessage msg : items) {
                    if (msg.getSnippet() == null || !"textMessageEvent".equals(msg.getSnippet().getType())) {
                        continue;
                    }
                    if (msg.getSnippet().getTextMessageDetails() == null) continue;

                    Chats chat = new Chats();
                    chat.setUsername(msg.getAuthorDetails() != null ? msg.getAuthorDetails().getDisplayName() : "unknown");
                    chat.setSource("youtube");
                    chat.setMessage(msg.getSnippet().getTextMessageDetails().getMessageText());
                    chat.setTimestamp(Instant.now());

                    service.save(chat);
                }
            }

            // Respect YouTube's dynamic rate-limiting interval suggestion
            if (response.getPollingIntervalMillis() != null) {
                nextDelay = response.getPollingIntervalMillis();
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("[YouTube] API error status: " + e.getStatusCode() + " - " + e.getDetails().getMessage());
            nextDelay = 15000; // Back off longer if hitting quotas or stream errors
        } catch (Exception e) {
            System.err.println("[YouTube] Poll error: " + e.getMessage());
            nextDelay = 10000;
        } finally {
            if (running) {
                scheduledTask = scheduler.schedule(this::pollAndReschedule, nextDelay, TimeUnit.MILLISECONDS);
            }
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdownNow();
    }
}
