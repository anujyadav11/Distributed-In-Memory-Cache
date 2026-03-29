package com.anuj.cache.api.service;

import com.anuj.cache.core.LRUCache;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class CacheService {

    private static final Logger logger =
            Logger.getLogger(CacheService.class.getName());

    private final LRUCache<String, String> cache;
    private final long startTime = System.currentTimeMillis();

    public CacheService(LRUCache<String, String> cache) {
        this.cache = cache;
        logger.info("HTTP Cache instance: " + cache.hashCode());
    }
    // ✅ PUT
    public void put(String key, String value) {
        validateKey(key);
        if(value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        value = value.replace("|", ""); // Replace pipes with empty strings
        long version = System.currentTimeMillis();
        cache.put(key, value + "|" + version);
    }
    // ✅ PUT with TTL
    public void putWithTTL(String key, String value, long ttl) {
        validateKey(key);
        if (ttl <= 0) {
            throw new IllegalArgumentException("TTL must be greater than 0");
        }
        cache.put(key, value, ttl);
    }
    // ✅ GET
    public String get(String key) {
        validateKey(key);
        String val = cache.get(key);
        if(val == null ) return null;
        int sepIndex = val.lastIndexOf("|");
        if (sepIndex == -1) {
            return val; // No version info, return as is
        }
        return val.substring(0, sepIndex); // Return only the value, ignore version
    }
    // ✅ DELETE
    public void delete(String key) {
        validateKey(key);
        cache.delete(key);
    }
    // ✅ STATS
    public String getStats() {
        var metrics = cache.getMetrics();

        long hits = metrics.getHits();
        long misses = metrics.getMisses();
        long total = hits + misses;

        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;
        long uptimeSeconds =
                (System.currentTimeMillis() - startTime) / 1000;

        return "hits=" + hits +
                ",misses=" + misses +
                ",hitRate=" + String.format("%.2f", hitRate) + "%" +
                ",evictions=" + metrics.getEvictions() +
                ",expirations=" + metrics.getExpirations() +
                ",uptime=" + uptimeSeconds + "s" +
                ",capacity=" + cache.capacity() +
                ",size=" + cache.size();
    }
    // ✅ VALIDATION (important for API safety)
    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }
}