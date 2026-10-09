package com.arcyriea_loreverse.oracle_cloud.configs;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;


@Configuration
public class RedisConfig {

    private RedisServer redisServer;

    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @PostConstruct
    public void startEmbeddedRedis() {
        boolean isSecureOrRemote = redisUrl != null && (redisUrl.startsWith("rediss://") || !redisUrl.contains("localhost"));

        if (isSecureOrRemote) {
            System.out.println("======> Remote/HTTPS cloud environment detected: Bypassing local embedded Redis process...");
        }
        else {
            System.out.println("======> Local environment detected: Spinning up Managed Redis Server (v7.x)...");
            try {
                assert redisUrl != null;
                URI uri = URI.create(redisUrl);
                int port = uri.getPort();

                redisServer = RedisServer.builder()
                        .port(port)
                        .setting("bind 127.0.0.1") //
                        .setting("maxmemory 128M")
                        .build();

                redisServer.start();
                waitForRedisToBeReady("127.0.0.1", port);

                System.out.println("======> Managed Redis successfully started on port "+port+"!");
            } catch (Exception e) {
                System.err.println("CRITICAL: Failed to launch local native Redis process: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            System.out.println("======> Managed Redis Server process gracefully terminated.");
        }
    }

    private void waitForRedisToBeReady(String host, int port) {
        int retries = 10;
        int waitTimeMs = 150;

        for (int i = 0; i < retries; i++) {
            try (Socket socket = new Socket(host, port)) {
                // Connection succeeded, port is listening!
                return;
            } catch (IOException e) {
                // Not ready yet, wait a tiny bit and retry
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.err.println("WARNING: Redis port " + port + " did not respond within timeout limits. Connection factory initialization might fail.");
    }
}