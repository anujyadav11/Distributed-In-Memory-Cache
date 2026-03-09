package com.anuj.cache.server;

import com.anuj.cache.core.LRUCache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CacheServer {

    private final LRUCache<String, String> cache = new LRUCache<>(1000);

    public void start(int port) throws Exception {

        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Cache server started on port " + port);

        while (true) {

            Socket clientSocket = serverSocket.accept();

            new Thread(() -> handleClient(clientSocket)).start();
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

        String[] parts = command.split(" ");

        String action = parts[0].toUpperCase();

        switch (action) {

            case "PUT":
                cache.put(parts[1], parts[2]);
                return "OK";

            case "GET":
                String value = cache.get(parts[1]);
                return value == null ? "NULL" : value;

            case "DELETE":
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