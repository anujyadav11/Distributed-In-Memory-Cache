package com.anuj.cache.persistence;

import com.anuj.cache.core.CacheEntry;
import com.anuj.cache.core.LRUCache;

import java.io.*;
import java.util.Map;

public class SnapshotManager {

    private final String snapshotFilePath;

    public SnapshotManager(String snapshotFilePath) {
        this.snapshotFilePath = snapshotFilePath;
    }

    // Save cache to disk
    public void saveSnapshot(LRUCache cache) {

        try (BufferedWriter writer =
                    new BufferedWriter(new FileWriter(snapshotFilePath))) {
            Map<String, CacheEntry<String>> entries =
            cache.getEntriesForSnapshot();

    for (Map.Entry<String, CacheEntry<String>> entry : entries.entrySet()) {

            String key = entry.getKey();
            CacheEntry<String> value = entry.getValue();

        writer.write(key + "=" +
            value.getValue() + "|" +
            value.getExpiryTime());

            writer.newLine();
    }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load cache from disk
    public void loadSnapshot(LRUCache cache) {

        File file = new File(snapshotFilePath);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                    new BufferedReader(new FileReader(snapshotFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                if (parts.length == 2) {

                    String key = parts[0];

                    String[] valueParts = parts[1].split("\\|");

                    if (valueParts.length == 2) {

                        String value = valueParts[0];
                        long expiry =
                                Long.parseLong(valueParts[1]);

                        cache.putRecovered(key, value, expiry);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}