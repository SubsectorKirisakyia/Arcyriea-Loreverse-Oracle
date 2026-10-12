package com.arcyriea_loreverse.oracle_cloud.crud.services;

import com.arcyriea_loreverse.oracle_cloud.properties.YoutubeProperties;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "youtube",
        name = {"channel-id", "api.key"}
)
public class YoutubeLiveService {
    private final YoutubeProperties properties;
    private YouTube youtube;

    @PostConstruct
    public void init() throws Exception {
        this.youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                null) // no HTTP request initializer needed when using API key
                .setApplicationName("lythiariena-youtube-listener")
                .build();
    }

    public String resolveLiveChatId() throws IOException {
        // 1. Search for currently live video
        YouTube.Search.List search = youtube.search()
                .list(List.of("snippet"));

        search.setChannelId(properties.channelId());
        search.setEventType("live");
        search.setType(Collections.singletonList("video"));
        search.setMaxResults(1L);
        search.setKey(properties.api().key());

        SearchListResponse searchResponse = search.execute();

        if (searchResponse.getItems() == null || searchResponse.getItems().isEmpty()) {
            throw new IllegalStateException("No live stream found on channel " + properties.channelId());
        }

        String videoId = searchResponse.getItems().getFirst().getId().getVideoId();

        // 2. Get liveStreamingDetails → activeLiveChatId
        YouTube.Videos.List videoRequest = youtube.videos()
                .list(List.of("liveStreamingDetails"));

        videoRequest.setId(List.of(videoId));
        videoRequest.setKey(properties.api().key());

        VideoListResponse videoResponse = videoRequest.execute();

        if (videoResponse.getItems() == null || videoResponse.getItems().isEmpty()) {
            throw new IllegalStateException("Could not retrieve video details for " + videoId);
        }

        String liveChatId = videoResponse.getItems().getFirst()
                .getLiveStreamingDetails()
                .getActiveLiveChatId();

        if (liveChatId == null || liveChatId.isBlank()) {
            throw new IllegalStateException("Stream is live but has no active live chat");
        }

        return liveChatId;
    }
}
