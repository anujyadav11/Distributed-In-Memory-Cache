package com.anuj.cache.persistence;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import com.anuj.cache.core.LRUCache;

public class WALManager {
    private final String logFile;

    public WALManager(String logFile) {
        this.logFile = logFile;
    }
    public synchronized void log(String command){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(command);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void replay(LRUCache<String, String> cache) {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 3);
                String action = parts[0];
                switch(action){

                    case "PUT":
                        if(parts.length == 3){
                            cache.put(parts[1], parts[2]);
                        }
                        break;
                    case "DELETE":
                        if(parts.length == 2){
                            cache.delete(parts[1]);
                        }
                        break;
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
