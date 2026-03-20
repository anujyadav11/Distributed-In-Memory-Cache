# 🚀 Distributed In-Memory Cache (Java)

A high-performance, thread-safe distributed in-memory cache inspired by systems like Redis.
Built from scratch in Java with a strong focus on **system design, concurrency, and persistence**.

---

## ✨ Features

- ⚡ **LRU (Least Recently Used) eviction** – O(1) get/put operations
- ⏱️ **TTL (Time-To-Live)** support with automatic expiration
- 🔒 **Thread-safe design** using `ReadWriteLock`
- 🌐 **TCP-based cache server** for client communication
- 🧠 **Command protocol**: PUT, GET, DELETE, STATS
- 💾 **Snapshot persistence** (disk-based recovery)
- 📝 **Write-Ahead Logging (WAL)** for durability
- ♻️ **WAL compaction** to prevent unbounded log growth
- 🛡️ **Atomic snapshot writes** (crash-safe persistence)
- ⚙️ **Configurable cache capacity** (command-line args + environment variables)
- 📊 **Metrics & observability**
  - hits / misses
  - hit rate
  - evictions
  - expirations
  - uptime

- 🖥️ **Interactive CLI client**
- 📈 **Benchmarking tool** for QPS measurement

---

## 🏗️ Architecture

```
Client
  ↓
TCP Cache Server
  ↓
Command Processor
  ↓
LRU Cache Engine
  ↓
Persistence Layer
   ├─ Snapshot (RDB-style)
   └─ WAL (AOF-style)
```

---

### Persistence Flow

```
Write operation
   ↓
WAL (append log)
   ↓
Cache update
   ↓
Periodic snapshot
   ↓
WAL reset (compaction)
```

---

### Recovery Flow

```
Server restart
   ↓
Load snapshot
   ↓
Replay WAL
   ↓
Cache restored
```

---

## ⚙️ How to Run

### 1️⃣ Compile

```bash
mvn clean compile
```

---

### 2️⃣ Start Cache Server

```bash
java com.anuj.cache.server.CacheServer 1000
```

- `1000` = cache capacity
- Optional via environment variable:

```bash
export CACHE_SIZE=5000
```

Priority:

```
args > env > default
```

---

### 3️⃣ Run Client

```bash
java com.anuj.cache.client.CacheClient
```

---

## 💻 Supported Commands

```
PUT <key> <value>
GET <key>
DELETE <key>
STATS
```

---

## 📊 Example Usage

```
> PUT user1 Anuj
→ SUCCESS: OK

> GET user1
→ VALUE: Anuj

> GET user2
→ NULL

> DELETE user1
→ SUCCESS: OK

> STATS
→ hits=2,misses=1,hitRate=66.67%,evictions=0,expirations=0,size=1,uptime=45s
```

---

## 📈 Performance Benchmark

Measured using a multithreaded benchmark client.

- Threads: 10
- Requests per thread: 1000
- Total requests: 20,000
- QPS: **~15,000–20,000 ops/sec (local machine)**

### Benchmark Approach

- Concurrent threads simulate real-world load
- Mixed PUT + GET workload
- Throughput measured in QPS

---

## 🧠 Design Decisions

### 🔹 LRU Cache

Ensures efficient memory usage by evicting least recently used entries while maintaining O(1) operations.

### 🔹 TTL Support

Entries expire automatically using timestamp-based validation and background cleanup.

### 🔹 WAL (Write-Ahead Log)

Guarantees durability by logging operations before applying them to memory.

### 🔹 Snapshot Persistence

Periodically saves full cache state to disk for faster recovery.

### 🔹 Atomic Snapshots

Snapshots are written to a temporary file and atomically renamed to prevent corruption.

### 🔹 WAL + Snapshot Strategy

Inspired by Redis persistence model:

- WAL → ensures durability
- Snapshot → enables fast recovery

### 🔹 Thread Safety

- Optimized for read-heavy workloads using `ReadWriteLock`
- Concurrent request handling via thread pool

---

## 🎯 Why This Project

This project demonstrates:

- Strong system design fundamentals
- Building a Redis-like cache from scratch
- Concurrency and synchronization in Java
- Real-world persistence strategies (WAL + Snapshot)
- Performance testing and benchmarking
- Production-level backend engineering practices

---

## 📁 Project Structure

```
src/main/java/com/anuj/cache
├── core
│   ├── LRUCache
│   ├── CacheEntry
│   ├── Node
│   └── CacheMetrics
│
├── server
│   └── CacheServer
│
├── persistence
│   ├── SnapshotManager
│   └── WALManager
│
├── client
│   ├── CacheClient
│   └── CacheBenchmark
```

---

## 🔮 Future Improvements

- 🌐 Distributed cache (multi-node)
- 🔁 Replication (leader-follower)
- ⚖️ Consistent hashing
- 🌍 HTTP/REST API layer
- 📦 Docker containerization
- 📡 Metrics integration (Prometheus / Grafana)

---

## 🧑‍💻 Author

**Anuj Yadav**

---

## ⭐ If you found this useful

Give it a ⭐ on GitHub!
