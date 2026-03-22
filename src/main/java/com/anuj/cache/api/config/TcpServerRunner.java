package com.anuj.cache.api.config;

import com.anuj.cache.core.LRUCache;
import com.anuj.cache.server.CacheServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpServerRunner {
    // ✅ Read TCP port from application.properties
    @Value("${cache.tcp.port}")
    private int port;
    /**
     * ✅ Create CacheServer as Spring Bean
     * This ensures SAME instance is used across app
     */
    @Bean
    public CacheServer cacheServer(LRUCache<String, String> cache) {
        return new CacheServer(port, cache);
    }
    /**
     * ✅ Start TCP server when Spring Boot starts
     */
    @Bean
    public CommandLineRunner startTcpServer(CacheServer server) {
        return args -> {
            System.out.println("Starting TCP server on port " + port);
            server.start();
        };
    }
}