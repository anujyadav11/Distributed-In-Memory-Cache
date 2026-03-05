package com.anuj.cache.core;

class Node<K, V> {

    K key;
    CacheEntry<V> entry;

    Node<K, V> prev;
    Node<K, V> next;

    Node(K key, CacheEntry<V> entry) {
        this.key = key;
        this.entry = entry;
    }
}