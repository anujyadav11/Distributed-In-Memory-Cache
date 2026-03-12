package com.anuj.cache.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CacheServerTest {

    private final CacheServer server = new CacheServer();

    @Test
    void testPutAndGet() {

        String put = server.processCommand("PUT name Anuj");
        assertEquals("OK", put);

        String value = server.processCommand("GET name");
        assertEquals("Anuj", value);
    }

    @Test
    void testDelete() {

        server.processCommand("PUT city London");

        String del = server.processCommand("DELETE city");
        assertEquals("OK", del);

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