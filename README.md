# Distributed In-Memory Cache (Java)

A high-performance in-memory cache server built from scratch in Java.

The project demonstrates core systems engineering concepts, including:

- LRU eviction
- TTL expiration
- background cleanup workers
- thread-safe cache design
- TCP-based cache server
- command protocol
- metrics and observability

This project is inspired by systems like Redis and Memcached.

## Features

- O(1) GET and PUT operations
- LRU eviction policy
- TTL (time-to-live) support
- background expiration worker
- thread-safe cache using ReadWriteLock
- TCP cache server
- command protocol (PUT, GET, DELETE)
- metrics tracking (hits, misses, evictions)

## Architecture

          Client
            │
            │ TCP
            ▼
      Cache Server
            │
            ▼
       Command Parser
            │
            ▼
        Cache Engine
            │
      ┌─────┴─────┐
      ▼           ▼

ConcurrentMap LRU List
│
▼
CacheEntry
(value + TTL)

## Example Commands

PUT name Anuj Yadav
GET name
DELETE name

PUT name Anuj Yadav
→ OK

GET name
→ Anuj Yadav

DELETE name
→ OK

## Running the Server

Compile the project:

mvn clean compile

Start the cache server:

java -cp target/classes com.anuj.cache.server.CacheServer

Run the test client:

java -cp target/classes com.anuj.cache.client.CacheClient

## Future Improvements

- distributed cache cluster
- consistent hashing
- replication
- HTTP interface
- persistence
