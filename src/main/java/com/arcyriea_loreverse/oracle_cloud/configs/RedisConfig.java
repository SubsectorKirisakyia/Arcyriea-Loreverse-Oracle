package com.arcyriea_loreverse.oracle_cloud.configs;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.util.Arrays;
import java.util.List;


@Configuration
public class RedisConfig {

    private RedisServer redisServer;

    @Value("${spring.data.redis.url}")
    private String redisUrl;

    @PostConstruct
    public void startEmbeddedRedis() {
        if (redisUrl != null && redisUrl.contains("localhost")) {

            System.out.println("======> Local environment detected: Spinning up Managed Redis Server (v7.x)...");
            try {
                List<String> urlParts = Arrays.stream(redisUrl.split(":")).toList();
                int port = Integer.parseInt(urlParts.getLast());

                redisServer = RedisServer.builder()
                        .port(port)
                        .setting("bind 127.0.0.1") //
                        .setting("maxmemory 128M")
                        .build();

                redisServer.start();
                System.out.println("======> Managed Redis successfully started on port "+port+"!");
            } catch (Exception e) {
                System.err.println("CRITICAL: Failed to launch local native Redis process: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("======> Remote cloud environment detected: Bypassing local processes to target Render Key Value...");
        }
    }

    @PreDestroy
    public void stopEmbeddedRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
            System.out.println("======> Managed Redis Server process gracefully terminated.");
        }
    }
}