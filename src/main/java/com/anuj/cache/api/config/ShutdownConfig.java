package com.anuj.cache.api.config;

import com.anuj.cache.server.CacheServer;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShutdownConfig {
    private final CacheServer cacheServer;

    public ShutdownConfig(CacheServer cacheServer) {
        this.cacheServer = cacheServer;
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("Shutting down TCP server...");
        cacheServer.stop();
    }
}