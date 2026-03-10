package com.anuj.cache.server;

import com.anuj.cache.core.LRUCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CacheServer {

    private final LRUCache<String, String> cache = new LRUCache<>(1000);
    private final Logger logger = Logger.getLogger(CacheServer.class.getName());

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void start(int port) throws Exception {

        ServerSocket serverSocket = new ServerSocket(port);

        logger.info("Cache server started on port " + port);

        while (true) {

            Socket clientSocket = serverSocket.accept();
            logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());
            threadPool.submit(() -> handleClient(clientSocket));
        }
    }

    private void handleClient(Socket socket) {

        try {

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);

            String command;

            while ((command = in.readLine()) != null) {

                String response = processCommand(command);

                out.println(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String processCommand(String command) {
        if(command == null || command.trim().isEmpty()) {
            return "ERROR: Empty command";
        }
        logger.info("Command received: " + command);
        String[] parts = command.trim().split("\\s+", 3);
        String action = parts[0].toUpperCase();
        switch (action) {

            case "PUT":
                if (parts.length < 3) {
                    return "ERROR: PUT command requires key and value";
                }
                cache.put(parts[1], parts[2]);
                return "OK";

            case "GET":
                if (parts.length < 2) {
                    return "ERROR: GET command requires key";
                }
                String value = cache.get(parts[1]);
                return value == null ? "NULL" : value;

            case "DELETE":
                if (parts.length < 2) {
                    return "ERROR: DELETE command requires key";
                }
                cache.delete(parts[1]);
                return "OK";

            default:
                return "UNKNOWN_COMMAND";
        }
    }
    public static void main(String[] args) throws Exception {

        CacheServer server = new CacheServer();

        server.start(8080);
    }
}