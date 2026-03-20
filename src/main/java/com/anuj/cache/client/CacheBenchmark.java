package com.anuj.cache.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class CacheBenchmark {

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private static final int THREADS = 50;
    private static final int REQUESTS_PER_THREAD = 5000;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREADS);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < THREADS; i++) {
            new Thread(() -> {
                try (
                    Socket socket = new Socket(HOST, PORT);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                            socket.getOutputStream(), true)
                ) {
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        String key = "key" + j;
                        out.println("SET " + key + " value" + j);
                        in.readLine(); // Read response
                        out.println("GET " + key);
                        in.readLine(); // Read response
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        long totalRequests = THREADS * REQUESTS_PER_THREAD * 2; // SET + GET
        double seconds = (endTime - startTime) / 1000.0;
        double qpq = totalRequests / seconds;
        System.out.printf("Total Requests: %d\n", totalRequests);
        System.out.printf("Total Time: %.2f seconds\n", seconds);
        System.out.printf("Queries per Second: %.2f\n", qpq);
        System.out.println("Benchmark completed!");
    }
}
