package com.anuj.cache.api.config;

import com.anuj.cache.core.LRUCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean
    public LRUCache<String, String> cache() {
        return new LRUCache<>(1000);
    }
}