package com.anuj.cache.api.config;

import com.anuj.cache.core.LRUCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class CacheConfig {
    @Value("${cache.capacity}")
    private int capacity;

    @Bean
    public LRUCache<String, String> cache() {
        return new LRUCache<>(capacity);
    }
}