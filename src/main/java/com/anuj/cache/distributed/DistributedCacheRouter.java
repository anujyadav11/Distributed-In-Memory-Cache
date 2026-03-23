package com.anuj.cache.distributed;

public class DistributedCacheRouter {
    private final ConsistentHashing Hashing;

    public DistributedCacheRouter(ConsistentHashing Hashing) {
        this.Hashing = Hashing;
    }
    public String route(String key) {
        return Hashing.getNode(key);
    }
}