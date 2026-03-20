package com.anuj.cache.api.service;

import com.anuj.cache.core.LRUCache;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private final LRUCache<String, String> cache;

    public CacheService(LRUCache<String, String> cache) {
        this.cache = cache;
    }

    public void put(String key, String value) {
        cache.put(key, value);
    }

    public String get(String key) {
        return cache.get(key);
    }
    public void delete(String key) {
        cache.delete(key);
    }
}

