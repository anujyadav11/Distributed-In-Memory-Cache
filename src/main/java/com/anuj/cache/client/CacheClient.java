package com.anuj.cache.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CacheClient {

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("localhost", 8080);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);

        out.println("PUT name Anuj Yadav");
        System.out.println("Response: " + in.readLine());

        out.println("GET name");
        System.out.println("Response: " + in.readLine());

        out.println("DELETE name");
        System.out.println("Response: " + in.readLine());

        socket.close();
    }
}