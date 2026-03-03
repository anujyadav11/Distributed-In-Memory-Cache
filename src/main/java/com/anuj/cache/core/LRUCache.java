package com.anuj.cache.core;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> implements Cache<K, V> {

    private final int capacity;
    private final Map<K, Node<K, V>> map;

    private Node<K, V> head;
    private Node<K, V> tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    @Override
    public void put(K key, V value) {

        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            moveToHead(node);
            return;
        }

        if (map.size() >= capacity) {
            evict();
        }

        Node<K, V> newNode = new Node<>(key, value);
        addToHead(newNode);
        map.put(key, newNode);
    }

    @Override
    public V get(K key) {
        Node<K, V> node = map.get(key);

        if (node == null) return null;

        moveToHead(node);
        return node.value;
    }

    @Override
    public void delete(K key) {
        Node<K, V> node = map.remove(key);

        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    private void evict() {
        if (tail == null) return;

        map.remove(tail.key);
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
    }
}