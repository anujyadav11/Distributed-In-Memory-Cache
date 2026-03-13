package com.anuj.cache.server;

import com.anuj.cache.core.LRUCache;
import com.anuj.cache.persistence.SnapshotManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.logging.Logger;

public class CacheServer {

    private final LRUCache<String, String> cache = new LRUCache<>(1000);

    private static final Logger logger =
            Logger.getLogger(CacheServer.class.getName());

    private final ExecutorService threadPool =
            Executors.newFixedThreadPool(10);

    private SnapshotManager snapshotManager;

    // CONSTRUCTOR
    public CacheServer() {

        snapshotManager = new SnapshotManager("cache_snapshot.txt");

        // Load snapshot on startup
        snapshotManager.loadSnapshot(cache);

        // Start periodic snapshot saving
        startSnapshotScheduler();
    }

    private void startSnapshotScheduler() {

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(
                () -> snapshotManager.saveSnapshot(cache),
                30,
                30,
                TimeUnit.SECONDS
        );
    }

    public void start(int port) throws Exception {

        ServerSocket serverSocket = new ServerSocket(port);

        logger.info("Cache server started on port " + port);

        while (true) {

            Socket clientSocket = serverSocket.accept();

            logger.info("Client connected: "
                    + clientSocket.getRemoteSocketAddress());

            threadPool.submit(() -> handleClient(clientSocket));
        }
    }

    private void handleClient(Socket socket) {

        try {

            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);

            String command;

            while ((command = in.readLine()) != null) {

                String response = processCommand(command);

                out.println(response);
            }

            socket.close();

        } catch (Exception e) {

            logger.warning("Error handling client: " + e.getMessage());
        }
    }

    public String processCommand(String command) {

        if (command == null || command.trim().isEmpty()) {
            return "ERROR: Empty command";
        }

        String[] parts = command.trim().split("\\s+", 3);

        String action = parts[0].toUpperCase();

        switch (action) {

            case "PUT":
                if (parts.length < 3)
                    return "ERROR: PUT requires key and value";

                cache.put(parts[1], parts[2]);
                return "OK";

            case "GET":
                if (parts.length < 2)
                    return "ERROR: GET requires key";

                String value = cache.get(parts[1]);
                return value == null ? "NULL" : value;

            case "DELETE":
                if (parts.length < 2)
                    return "ERROR: DELETE requires key";

                cache.delete(parts[1]);
                return "OK";

            case "STATS":
                return getStats();

            default:
                return "ERROR: Unknown command";
        }
    }

    private String getStats() {

        var metrics = cache.getMetrics();

        return "hits=" + metrics.getHits() +
                ",misses=" + metrics.getMisses() +
                ",evictions=" + metrics.getEvictions() +
                ",expirations=" + metrics.getExpirations() +
                ",size=" + cache.size();
    }

    public static void main(String[] args) throws Exception {

        CacheServer server = new CacheServer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            server.threadPool.shutdown();

            System.out.println("Cache server stopped.");
        }));

        server.start(8080);
    }
}