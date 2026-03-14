package com.anuj.cache.persistence;

import com.anuj.cache.core.LRUCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SnapshotManagerTest {

    @Test
    public void testSaveAndLoadSnapshot() {

        String filePath = "test_snapshot.dat";

        LRUCache<String, String> cache = new LRUCache<>(2);
        cache.put("user1", "Anuj", -1);
        cache.put("user2", "Rahul", -1);
        SnapshotManager manager = new SnapshotManager(filePath);
        manager.saveSnapshot(cache);

        LRUCache recoveredCache = new LRUCache<>(10);
        manager.loadSnapshot(recoveredCache);
        assertEquals("Anuj", recoveredCache.get("user1"));
        assertEquals("Rahul", recoveredCache.get("user2"));

        // Clean up
        //new java.io.File(filePath).delete();
    }
}