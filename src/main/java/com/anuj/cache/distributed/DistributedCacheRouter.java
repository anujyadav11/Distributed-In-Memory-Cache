package com.anuj.cache.distributed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
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

        int successCount = 0;

        String res1 = client.send(primary, "PUT " + key + " " + value);
        if(!res1.startsWith("ERROR")) {
            successCount++;
        }

        String res2 = client.send(replica, "PUT " + key + " " + value);
        if(!res2.startsWith("ERROR")) {
            successCount++;
        }

        if(successCount >= 1) {
            return "SUCCESS (quorum achieved)";
        }
        return "ERROR: WRITE_FAILED";
    }

public String get(String key) {
        if (key == null || key.isBlank()) {
            return "ERROR: INVALID_KEY";
        }
        String primary = hashing.getNode(key);
        String replica = getReplicaNode(key);
        Callable<String> primaryCall = () -> {
            System.out.println("Trying primary: " + primary);
            return client.send(primary, "GET " + key);
        };
        Callable<String> replicaCall = () -> {
            System.out.println("Trying replica: " + replica);
            return client.send(replica, "GET " + key);
        };
        CompletionService<String> completionService =
            new ExecutorCompletionService<>(executor);
        completionService.submit(primaryCall);
        completionService.submit(replicaCall);
    try {
        for (int i = 0; i < 2; i++) {
            Future<String> future = completionService.take(); // returns FIRST completed
            String res = future.get();
            if (res != null && !res.startsWith("ERROR") && !res.equals("NULL")) {
                return res; // ✅ fastest successful response
            }
        }
        } catch (Exception e) {
            return "ERROR: PARALLEL_READ_FAILED";
        }
        return "NULL";
    }

    public String delete(String key) {
        String primary = hashing.getNode(key);
        String replica = getReplicaNode(key);

        String res1 = client.send(primary, "DELETE " + key);
        String res2 = client.send(replica, "DELETE " + key);

        return "Primary: " + res1 + ", Replica: " + res2;
    }

    public String getReplicaNode(String key) {

        String primary = hashing.getNode(key);

        var ring = hashing.getRing();
        var entry = ring.ceilingEntry(hashing.getHash(primary + "_replica"));

        if(entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }
    public String tryReplica(String key) {
        String replica = getReplicaNode(key);

        String response = client.send(replica, "GET " + key);

        if(!response.startsWith("ERROR")){
            healthTracker.markUnhealthy(replica);
            return response;
        }

        return "ERROR: ALL_NODES_FAILED";
    }
}