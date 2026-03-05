package com.anuj.cache.core;
/**
 * Wrapper for cached value that also stores expiration metadata.
 */
public class CacheEntry<V> {
    private final V value;
    private final long expiryTime;
    public CacheEntry(V value, long ttlMillis) {
        this.value = value;
        if (ttlMillis > 0) {
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        } else {
            this.expiryTime = -1;
        }
    }
    public V getValue() {
        return value;
    }
    public boolean isExpired() {
        return expiryTime != -1 && System.currentTimeMillis() > expiryTime;
    }
}