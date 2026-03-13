package com.anuj.cache.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final ConcurrentHashMap<K, Node<K, V>> map;

    private Node<K, V> head;
    private Node<K, V> tail;
    private final ScheduledExecutorService cleaner =
        Executors.newSingleThreadScheduledExecutor();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ChacheMetrics metrics = new ChacheMetrics();

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        startCleaner();
    }
    public Map<K, CacheEntry<V>> getEntriesForSnapshot() {
        Map<K, CacheEntry<V>> snapshot = new HashMap<>();
        for (Map.Entry<K, Node<K,V>> entry : map.entrySet()) {
            Node<K,V> node = entry.getValue();
            snapshot.put(entry.getKey(), node.entry);
    }
        return snapshot;
    }
    @Override
    public void put(K key, V value) {
        putEntry(key, new CacheEntry<>(value, -1));
    }

    public void put(K key, V value, long ttlMillis) {
        putEntry(key, new CacheEntry<>(value, ttlMillis));
    }

    private void putEntry(K key, CacheEntry<V> entry) {

        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.entry = entry;
            moveToHead(node);
            return;
        }

        if (map.size() >= capacity) {
            evict();
        }

        Node<K, V> newNode = new Node<>(key, entry);

        addToHead(newNode);
        map.put(key, newNode);
    }

    public void putRecovered(K key, V value, long expiryTime) {
        lock.writeLock().lock();
        try {
            CacheEntry<V> entry = new CacheEntry<>(value, expiryTime);
                Node<K, V> newNode = new Node<>(key, entry);

                map.put(key, newNode);
                
                addToHead(newNode);
        }
        finally {
            lock.writeLock().unlock();
        }
    }
    @Override
    public V get(K key) {
        lock.readLock().lock();
        try {
            Node<K, V> node = map.get(key);

            if (node == null){ 
                metrics.recordMiss();
                return null;
        }

        if (node.entry.isExpired()) {
            metrics.recordExpiration();
            delete(key);
            return null;
        }

        moveToHead(node);
        metrics.recordHit();
        return node.entry.getValue();
    }
        finally {
            lock.readLock().unlock();
        }
    }
    public ChacheMetrics getMetrics() {
        return metrics;
    }
    
    @Override
    public void delete(K key) {
        lock.writeLock().lock();
        try {
        Node<K, V> node = map.remove(key);

        if (node != null) {
            removeNode(node);
        }
    }
    finally {
        lock.writeLock().unlock();
    }
    }

    @Override
    public int size() {
        return map.size();
    }

    private void evict() {

        if (tail == null) return;

        map.remove(tail.key);
        metrics.recordEviction();
        removeNode(tail);
    }

    private void addToHead(Node<K, V> node) {

        node.next = head;
        node.prev = null;

        if (head != null) {
            head.prev = node;
        }

        head = node;

        if (tail == null) {
            tail = node;
        }
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    private void removeNode(Node<K, V> node) {

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.prev = null;
        node.next = null;
    }

    private void startCleaner() {
        cleaner.scheduleAtFixedRate(() -> {
            for(K key : map.keySet()) {
                Node<K, V> node = map.get(key);
                if (node != null && node.entry.isExpired()) {
                    delete(key);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    public void shutdown() {
        cleaner.shutdown();
    }
}