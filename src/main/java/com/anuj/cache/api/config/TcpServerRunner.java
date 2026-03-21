package com.anuj.cache.api.config;

import com.anuj.cache.core.LRUCache;
import com.anuj.cache.server.CacheServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpServerRunner {

    @Bean
    public CommandLineRunner startTcpServer(LRUCache<String, String> cache) {
        return args -> {
            CacheServer server = new CacheServer(9090, cache);
            server.start();
        };
    }
}