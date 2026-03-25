package com.anuj.cache.distributed;

import java.util.*;

public class ConsistentHashing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashing(List<String> nodes, int virtualNodes) {
        this.virtualNodes = virtualNodes;
        for (String node : nodes) {
            addNode(node);
        }
    }
    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            int hash = hash(node + "#" + i);
            System.out.println("Node: " + node + " Hash: " + hash);
            while(ring.containsKey(hash)) {
                hash = hash(hash + "collision"); // Linear probing
            }
            ring.put(hash, node);

        }
    }
    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            int hash = hash(node + "#" + i);
            ring.remove(hash);
        }
    }
    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        if(key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        int hash = hash(key);
        System.out.println("Key: " + key + " Hash: " + hash);
        var entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }
    public int hash(String key) {
    int h = key.hashCode();
    // 🔥 mix bits (important)
    h ^= (h >>> 16);
    h *= 0x85ebca6b;
    h ^= (h >>> 13);
    h *= 0xc2b2ae35;
    h ^= (h >>> 16);
    return h & 0x7fffffff;
}
    public int getRingSize() {
        return ring.size();
    }
    public TreeMap<Integer, String> getRing() {
        return ring;
    }
    public int hashPublic(String key) {
        return hash(key);
    }
}
