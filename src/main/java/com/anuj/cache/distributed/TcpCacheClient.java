package com.anuj.cache.distributed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class TcpCacheClient {

    private static final Logger logger =
            Logger.getLogger(TcpCacheClient.class.getName());

    public String send(String node, String command) {
        try {
            String[] parts = node.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            Socket socket = new Socket();

            // ✅ connection timeout
            socket.connect(new InetSocketAddress(host, port), 2000);

            // ✅ read timeout
            socket.setSoTimeout(2000);

            try (socket;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))) {

                out.println(command);

                String response = in.readLine();

                return response != null ? response : "ERROR: EMPTY_RESPONSE";
            }

        } catch (Exception e) {
            logger.warning("Error communicating with node " + node + ": " + e.getMessage());

            return "ERROR: NODE_UNAVAILABLE";
        }
    }
}