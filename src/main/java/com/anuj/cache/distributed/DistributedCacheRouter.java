package com.anuj.cache.distributed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.concurrent.*;
public class DistributedCacheRouter {

    private final ConsistentHashing hashing;
    private final TcpCacheClient client;
    private final NodeHealthTracker healthTracker;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public DistributedCacheRouter(ConsistentHashing hashing,
                                TcpCacheClient client,
                                NodeHealthTracker healthTracker) {
        this.hashing = hashing;
        this.client = client; // ✅ FIXED
        this.healthTracker = healthTracker; // ✅ FIXED
    }
    public String route(String key) {
        return hashing.getNode(key);
    }
    public String put(String key, String value) {
        String primary = hashing.getNode(key);
        String replica = getReplicaNode(key);

        long version = System.currentTimeMillis();
        String versionedValue = value + "|" + version;

        int successCount = 0;

        String res1 = client.send(primary, "PUT " + key + " " + versionedValue);
        if(!res1.startsWith("ERROR")) {
            successCount++;
        }

        String res2 = client.send(replica, "PUT " + key + " " + versionedValue);
        if(!res2.startsWith("ERROR")) {
            successCount++;
        }
        return successCount >= 1 ? "SUCCESS (quorum achieved)" : "ERROR: ALL_NODES_FAILED";
    }

public String get(String key) {

    if (key == null || key.isBlank()) {
        return "ERROR: INVALID_KEY";
    }

    String primary = hashing.getNode(key);
    String replica = getReplicaNode(key);

    CompletionService<Map.Entry<String, String>> completionService =
            new ExecutorCompletionService<>(executor);

    // ✅ return node + response
    completionService.submit(() ->
            Map.entry(primary, client.send(primary, "GET " + key)));

    completionService.submit(() ->
            Map.entry(replica, client.send(replica, "GET " + key)));

    String res1 = null, res2 = null;
    String node1 = null, node2 = null;
    System.out.println("Primary node → " + primary);
    System.out.println("Replica node → " + replica);
    try {
        for (int i = 0; i < 2; i++) {
            Future<Map.Entry<String, String>> future = completionService.take();
            Map.Entry<String, String> result = future.get();
            System.out.println("Raw response from node " + result.getValue());

            String node = result.getKey();
            String res = normalizeResponse(result.getValue()); //result.getValue();
            System.out.println("Normalized response from node " + node + ": " + res);
            if (res == null || res.startsWith("ERROR") || res.equals("NULL")) {
                continue;
            }

            if (res1 == null) {
                res1 = res;
                node1 = node;
            } else {
                res2 = res;
                node2 = node;
            }
        }

    } catch (Exception e) {
        return "ERROR: PARALLEL_READ_FAILED";
    }

    // ❌ no valid responses
    if (res1 == null && res2 == null) {
        return "NULL";
    }

    // ✅ only one valid response
    if (res2 == null) {
        return extractData(res1);
    }

    // 🔥 VERSION COMPARISON
    long v1 = extractVersion(res1);
    long v2 = extractVersion(res2);

    String latest = v1 >= v2 ? res1 : res2;
    String stale = v1 >= v2 ? res2 : res1;

    String latestNode = v1 >= v2 ? node1 : node2;
    String staleNode = v1 >= v2 ? node2 : node1;

    // 🔥 READ REPAIR
    if (v1 != v2) {
        System.out.println("Read repair triggered → fixing node: " + staleNode);

        client.send(staleNode, "PUT " + key + " " + extractData(latest));
    }

    return extractData(latest);
    }
    private String normalizeResponse(String res) {
        if (res == null ) {
            return "NULL";
        } 
        if(res.startsWith("VALUE :")) {
            return res.substring(7); // Strip "VALUE" prefix
        }
        return res;
    }
    public String delete(String key) {
        String primary = hashing.getNode(key);
        String replica = getReplicaNode(key);

        String res1 = client.send(primary, "DELETE " + key);
        String res2 = client.send(replica, "DELETE " + key);

        return "Primary: " + res1 + ", Replica: " + res2;
    }

public String getReplicaNode(String key) {

    int hash = hashing.getHash(key);

    var ring = hashing.getRing();

    var entry = ring.ceilingEntry(hash);
    if (entry == null) {
        entry = ring.firstEntry();
    }

    var next = ring.higherEntry(entry.getKey());
    if (next == null) {
        next = ring.firstEntry();
    }

    return next.getValue();
}
    public String tryReplica(String key) {
        String replica = getReplicaNode(key);

        String response = client.send(replica, "GET " + key);

        if(!response.startsWith("ERROR")){
            healthTracker.markHealthy(replica);
            return response;
        }

        return "ERROR: ALL_NODES_FAILED";
    }
    private long extractVersion(String value){
        if(value == null) return -1;
        int idx = value.lastIndexOf('|');
        if(idx == -1) return -1;
        try {
            return Long.parseLong(value.substring(idx + 1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    private String extractData(String value){
        if(value == null) return null;
        int idx = value.lastIndexOf('|');
        if(idx == -1) return value;
        return value.substring(0, idx);
    }
}