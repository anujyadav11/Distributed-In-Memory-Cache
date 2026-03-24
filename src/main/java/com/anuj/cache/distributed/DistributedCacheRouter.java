package com.anuj.cache.distributed;

public class DistributedCacheRouter {

    private final ConsistentHashing hashing;
    private final TcpCacheClient client;

    public DistributedCacheRouter(ConsistentHashing hashing,
                                TcpCacheClient client) {
        this.hashing = hashing;
        this.client = client; // ✅ FIXED
    }
    public String route(String key) {
        return hashing.getNode(key);
    }
    public String put(String key, String value) {
        String node = hashing.getNode(key);
        return client.send(node, "PUT " + key + " " + value);
    }

    public String get(String key) {
        String node = hashing.getNode(key);
        return client.send(node, "GET " + key);
    }

    public String delete(String key) {
        String node = hashing.getNode(key);
        return client.send(node, "DELETE " + key);
    }
}