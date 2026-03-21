package com.anuj.cache.server;

import org.junit.jupiter.api.Test;
import com.anuj.cache.core.LRUCache;

import static org.junit.jupiter.api.Assertions.*;

public class CacheServerTest {
    LRUCache<String, String> cache = new LRUCache<>(1000);
    private final CacheServer server = new CacheServer(9090, cache);

    @Test
    void testPutAndGet() {

        String put = server.processCommand("PUT name Anuj");
        assertEquals("SUCCESS: OK", put);

        String value = server.processCommand("GET name");
        assertEquals("VALUE: Anuj", value);
    }

    @Test
    void testDelete() {

        server.processCommand("PUT city London");

        String del = server.processCommand("DELETE city");
        assertEquals("SUCCESS: OK", del);

        String result = server.processCommand("GET city");
        assertEquals("NULL", result);
    }

    @Test
    void testStatsCommand() {

        server.processCommand("PUT key1 value1");
        server.processCommand("GET key1");

        String stats = server.processCommand("STATS");

        assertTrue(stats.contains("hits="));
        assertTrue(stats.contains("size="));
    }

    @Test
    void testInvalidCommand() {

        String response = server.processCommand("HELLO");

        assertEquals("ERROR: Unknown command", response);
    }
}