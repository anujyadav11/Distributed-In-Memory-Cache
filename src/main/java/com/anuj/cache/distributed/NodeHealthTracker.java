package com.anuj.cache.distributed;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeHealthTracker {

    private final Map<String, Boolean> nodeHealth = new ConcurrentHashMap<>();

    public void markHealthy(String node) {
        nodeHealth.put(node, true);
    }

    public void markUnhealthy(String node) {
        nodeHealth.put(node, false);
    }

    public boolean isHealthy(String node) {
        return nodeHealth.getOrDefault(node, true);
    }
}