package com.anuj.cache.eviction;

public interface EvictionPolicy<K> {

    void keyAccessed(K key);

    void keyAdded(K key);

    void keyRemoved(K key);

    K evictKey();
}