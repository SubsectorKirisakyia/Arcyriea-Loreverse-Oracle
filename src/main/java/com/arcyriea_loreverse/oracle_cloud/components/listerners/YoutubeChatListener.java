package com.arcyriea_loreverse.oracle_cloud.components.listerners;

import com.arcyriea_loreverse.oracle_cloud.crud.entities.mongo.Chats;
import com.arcyriea_loreverse.oracle_cloud.crud.services.YoutubeLiveService;
import com.arcyriea_loreverse.oracle_cloud.crud.services.mongo.UnifiedChatService;

import com.arcyriea_loreverse.oracle_cloud.properties.YoutubeProperties;
import io.grpc.*;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;

import youtube.api.v3.LiveChatMessageListRequest;
import youtube.api.v3.LiveChatMessageListResponse;
import youtube.api.v3.V3DataLiveChatMessageServiceGrpc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@ConditionalOnProperty(
        prefix = "youtube",
        name = {"channel-id", "api.key"}
)
@RequiredArgsConstructor
public class YoutubeChatListener {

    private final UnifiedChatService service;
    private final YoutubeLiveService liveChatIdService;
    private final SimpMessagingTemplate messagingTemplate;
    private final YoutubeProperties properties;

    private final ScheduledExecutorService reconnectExecutor =
            Executors.newSingleThreadScheduledExecutor();

    private ManagedChannel channel;
    private V3DataLiveChatMessageServiceGrpc.V3DataLiveChatMessageServiceStub asyncStub;

    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private volatile String currentPageToken = null;
    private volatile String liveChatId;

    @PostConstruct
    public void start() {
        System.out.println("[YouTube gRPC] Listener starting...");

        try {
            this.liveChatId = liveChatIdService.resolveLiveChatId();
            System.out.println("[YouTube gRPC] Resolved liveChatId = " + liveChatId);
        } catch (Exception e) {
            System.err.println("[YouTube gRPC] Failed to resolve liveChatId: " + e.getMessage());
            reconnectExecutor.schedule(this::start, 30, TimeUnit.SECONDS);
            return;
        }

        this.channel = ManagedChannelBuilder.forAddress("youtube.googleapis.com", 443)
                .useTransportSecurity()
                .build();

        // Inject Google API Key into Metadata headers
        Metadata headers = new Metadata();
        Metadata.Key<String> apiKeyHeader =
                Metadata.Key.of("x-goog-api-key", Metadata.ASCII_STRING_MARSHALLER);
        headers.put(apiKeyHeader, properties.api().key()
        );

        var headerInterceptor = MetadataUtils.newAttachHeadersInterceptor(headers);
        Channel interceptedChannel = ClientInterceptors.intercept(channel, headerInterceptor);

        this.asyncStub = V3DataLiveChatMessageServiceGrpc.newStub(interceptedChannel);

        connectStream();
    }

    private void connectStream() {
        if (liveChatId == null || liveChatId.isBlank()) {
            System.err.println("[YouTube gRPC] liveChatId is missing – cannot connect");
            return;
        }

        LiveChatMessageListRequest.Builder requestBuilder = LiveChatMessageListRequest.newBuilder()
                .setLiveChatId(liveChatId)
                .addPart("snippet")
                .addPart("authorDetails");

        if (currentPageToken != null) {
            requestBuilder.setPageToken(currentPageToken);
        }

        asyncStub.streamList(requestBuilder.build(), new StreamObserver<LiveChatMessageListResponse>() {

            @Override
            public void onNext(LiveChatMessageListResponse response) {
                currentPageToken = response.getNextPageToken();

                // Skip the initial historical backlog
                if (isInitialized.compareAndSet(false, true)) {
                    System.out.println("[YouTube gRPC] Initialized stream. Skipped backlog history.");
                    return;
                }

                for (var msg : response.getItemsList()) {
                    var snippet = msg.getSnippet();

                    if (!"textMessageEvent".equals(snippet.getType())) {
                        continue;
                    }

                    String authorName = (msg.hasAuthorDetails()
                            && !msg.getAuthorDetails().getDisplayName().isBlank())
                            ? msg.getAuthorDetails().getDisplayName()
                            : "unknown";

                    Chats chat = new Chats();
                    chat.setUsername(authorName);
                    chat.setSource("youtube");
                    chat.setMessage(snippet.getTextMessageDetails().getMessageText());

                    String publishedAtStr = snippet.getPublishedAt();
                    if (publishedAtStr != null && !publishedAtStr.isEmpty()) {
                        chat.setTimestamp(Instant.parse(publishedAtStr));
                    } else {
                        chat.setTimestamp(Instant.now());
                    }

                    messagingTemplate.convertAndSend("/topic/chats", service.save(chat));
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[YouTube gRPC] Stream error: " + t.getMessage() + ". Reconnecting in 5s...");
                // Optionally re-resolve liveChatId on error (in case stream ended / new stream started)
                reconnectExecutor.schedule(() -> {
                    try {
                        liveChatId = liveChatIdService.resolveLiveChatId();
                        isInitialized.set(false); // reset so we skip history again
                    } catch (Exception e) {
                        System.err.println("[YouTube gRPC] Could not re-resolve liveChatId: " + e.getMessage());
                    }
                    connectStream();
                }, 5, TimeUnit.SECONDS);
            }

            @Override
            public void onCompleted() {
                System.out.println("[YouTube gRPC] Stream ended by server. Reconnecting...");
                reconnectExecutor.schedule(YoutubeChatListener.this::connectStream, 1, TimeUnit.SECONDS);
            }
        });
    }



    @PreDestroy
    public void stop() {
        reconnectExecutor.shutdownNow();
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
