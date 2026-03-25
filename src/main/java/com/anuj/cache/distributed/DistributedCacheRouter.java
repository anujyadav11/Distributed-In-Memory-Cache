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
        String replica = getReplicaNode(key);

        String res1 = client.send(node, "PUT " + key + " " + value);
        String res2 = client.send(replica, "PUT " + key + " " + value);

        return "Primary: " + res1 + ", Replica: " + res2;
    }

    public String get(String key) {
        String primary = hashing.getNode(key);

        String response = client.send(primary, "GET " + key);

        if(response == null || response.startsWith("ERROR")) {
            String replica = getReplicaNode(key);

            System.out.println("Failover to replica: " + replica);

            response = client.send(replica, "GET " + key);
        }
        return response;
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
        var entry = ring.ceilingEntry(hashing.hash(primary + "_replica"));

        if(entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }
}