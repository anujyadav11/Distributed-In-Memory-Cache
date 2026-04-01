# 🚀 Distributed In-Memory Cache (Java)

A **distributed, fault-tolerant in-memory cache system** inspired by Redis, DynamoDB, and Cassandra.

Built from scratch in Java with focus on **distributed systems, concurrency, consistency, and real-world backend engineering**.

---

## ✨ Features

### ⚡ Core Cache Engine

- O(1) **LRU eviction** (HashMap + Doubly Linked List)
- ⏱️ **TTL (Time-To-Live)** with background cleanup worker
- 🔒 Thread-safe using `ReadWriteLock`

---

### 🌐 Distributed System

- 🧩 **Multi-node architecture** (TCP-based nodes)
- ⚖️ **Consistent hashing** for key distribution
- 🔁 **Replication (primary + replica nodes)**
- 🛡️ **Automatic failover**
- 🔄 **Retry mechanism for node failures**
- ❤️ **Node health tracking**

---

### ⚡ Performance Optimizations

- ⚡ **Parallel reads (hedged requests)** for low latency
- 📊 **Quorum-based writes** (fault-tolerant writes)
- 🎯 **Load-balanced reads**
- 🔧 **Read repair (auto-healing stale nodes)**

---

### 🧠 Consistency Model

- 🕒 **Versioning using timestamps**
- 🔄 **Eventual consistency**
- ⚔️ Conflict resolution using **latest-write-wins**

---

### 💾 Persistence Layer

- 📝 **Write-Ahead Logging (WAL)** for durability
- 💾 **Snapshot persistence (RDB-style)**
- ♻️ **WAL compaction**
- 🛡️ **Atomic snapshot writes (crash-safe)**

---

### 🌍 API Layer (Spring Boot)

- REST endpoints:
  - `POST /cache`
  - `GET /cache/{key}`
  - `DELETE /cache/{key}`
  - `GET /cache/cluster`

---

### 🎨 UI Dashboard

- Modern **web-based dashboard**
- Perform:
  - PUT / GET / DELETE operations

- View:
  - Cluster nodes
  - Live request logs

- Clean UX for demo and debugging

---

### 📊 Observability

- hits / misses
- hit rate
- evictions
- expirations
- uptime

---

## 🏗️ Architecture

```text
Browser UI
   ↓
Spring Boot HTTP API
   ↓
Distributed Cache Router
   ↓
Consistent Hash Ring
   ↓
TCP Cache Nodes (9001, 9002, 9003)
   ↓
LRU Cache Engine
   ↓
Persistence Layer (WAL + Snapshot)
```

---

## 🔄 Distributed Flow

### PUT (Write Path)

```text
Client → Router → Primary Node
                      ↓
                  Replica Node
```

- Writes are replicated to ensure durability
- **Quorum-based success** (at least one node must succeed)

---

### GET (Read Path)

```text
Client → Router
         ↓
   Parallel Read (Primary + Replica)
         ↓
   Fastest response returned
         ↓
   Read Repair (if inconsistency detected)
```

---

## 💾 Persistence Flow

```text
Write → WAL → Cache Update → Snapshot → WAL Compaction
```

---

## 🔁 Recovery Flow

```text
Restart → Load Snapshot → Replay WAL → Cache Restored
```

---

## ⚙️ How to Run

---

### 1️⃣ Build

```bash
mvn clean package
```

---

### 2️⃣ Start Distributed Nodes

Run 3 instances:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081 --cache.tcp.port=9001"

mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082 --cache.tcp.port=9002"

mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083 --cache.tcp.port=9003"
```

---

### 3️⃣ Open Dashboard

```
http://localhost:8081
```

---

## 📡 API Examples

### PUT

```http
POST /cache?key=user1&value=anuj
```

---

### GET

```http
GET /cache/user1
```

---

### DELETE

```http
DELETE /cache/user1
```

---

### CLUSTER

```http
GET /cache/cluster
```

---

## 📊 Performance

- ~15,000–20,000 QPS (local benchmark)
- Parallel reads reduce latency
- System remains available during node failures

---

## 🧠 Key Concepts Implemented

| Concept              | Description                |
| -------------------- | -------------------------- |
| Consistent Hashing   | Efficient key distribution |
| Replication          | Data redundancy            |
| Quorum Writes        | Fault-tolerant writes      |
| Parallel Reads       | Low-latency reads          |
| Read Repair          | Self-healing data          |
| Eventual Consistency | Distributed correctness    |
| WAL + Snapshot       | Durable persistence        |

---

## 📁 Project Structure

```text
src/main/java/com/anuj/cache
├── core
├── server
├── persistence
├── distributed
├── api
├── client
```

---

## 🎯 Why This Project

This project demonstrates:

- Distributed systems design (SDE-2 level)
- Building a Redis-like system from scratch
- Fault tolerance & replication strategies
- Concurrency in Java
- Real-world persistence (WAL + Snapshot)
- Performance optimization techniques
- Full-stack integration (Backend + UI)

---

## 🔮 Future Improvements

- Leader election (Raft / Zookeeper-style)
- Strong consistency mode
- Docker + Kubernetes deployment
- Prometheus + Grafana monitoring
- Cloud deployment (AWS/GCP)

---

## 🧑‍💻 Author

**Anuj Yadav**

---

## ⭐ If you found this useful

Give it a ⭐ on GitHub!
