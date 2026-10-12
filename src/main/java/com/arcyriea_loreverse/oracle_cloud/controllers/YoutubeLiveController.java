package com.arcyriea_loreverse.oracle_cloud.controllers;

import com.arcyriea_loreverse.oracle_cloud.crud.services.YoutubeLiveService;
import com.arcyriea_loreverse.oracle_cloud.properties.YoutubeProperties;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/auth/youtube")
@RequiredArgsConstructor
public class YoutubeLiveController {

    private final YoutubeLiveService liveService;

    @GetMapping()
    public ResponseEntity<?> getLiveChatId() {
        try {
            String liveChatId = liveService.resolveLiveChatId();
            return ResponseEntity.ok(Map.of("liveChatId", liveChatId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
