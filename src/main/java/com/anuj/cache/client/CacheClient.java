package com.anuj.cache.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CacheClient {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public static void main(String[] args) {

        try (
            Socket socket = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in))
        ) {

            System.out.println("Connected to Cache Server 🚀");
            System.out.println("Type 'help' for commands");

            String input;

            while (true) {

                System.out.print("> ");
                input = console.readLine();

                if (input == null) break;

                // Exit command
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Closing client...");
                    break;
                }

                // Help command
                if (input.equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                }

                // Send command to server
                out.println(input);

                // Read response
                String response = in.readLine();

                System.out.println("→ " + response);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void printHelp() {
        System.out.println("""
Available Commands:
PUT <key> <value>
GET <key>
DELETE <key>
STATS
exit
""");
    }
}