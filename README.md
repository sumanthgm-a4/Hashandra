# Regular Cassandra Architecture

## READ Operation (Partition Key Read)

```mermaid
sequenceDiagram
    participant Client
    participant Coordinator
    participant ReplicaA
    participant ReplicaB
    participant ReplicaC

    Client->>Coordinator: Read Request (partition_key = K)

    Note over Coordinator: Hash K → find replicas (A, B, C)

    Coordinator->>ReplicaA: Read Request
    Coordinator->>ReplicaB: Read Request
    Coordinator->>ReplicaC: Read Request

    ReplicaA-->>Coordinator: Data + Timestamp
    ReplicaB-->>Coordinator: Data + Timestamp
    ReplicaC-->>Coordinator: Data + Timestamp

    Note over Coordinator: Resolve conflicts (Last-Write-Wins)

    alt Data mismatch
        Coordinator->>ReplicaB: Read Repair (update stale)
        Coordinator->>ReplicaC: Read Repair (update stale)
    end

    Coordinator-->>Client: Final Result
```
---

## WRITE Operation 
```mermaid
sequenceDiagram
    participant Client
    participant Coordinator
    participant ReplicaA
    participant ReplicaB
    participant ReplicaC

    Client->>Coordinator: Write Request (K, Value)

    Note over Coordinator: Hash K → find replicas (A, B, C)

    Coordinator->>ReplicaA: Write (K, Value)
    Coordinator->>ReplicaB: Write (K, Value)
    Coordinator->>ReplicaC: Write (K, Value)

    ReplicaA-->>Coordinator: ACK
    ReplicaB-->>Coordinator: ACK
    ReplicaC-->>Coordinator: ACK

    Note over Coordinator: Wait for ACKs based on Consistency Level

    alt Some replicas down
        Coordinator->>Coordinator: Store Hint (Hinted Handoff)
    end

    Coordinator-->>Client: Success / Failure
```
---

# Hashandra (Mini Cassandra Clone)

## Overview
Hashandra is a simplified distributed key-value store inspired by Cassandra.

### Features
- Consistent Hashing (TreeMap-based)
- Replication (RF = 3)
- Coordinator-based writes
- Feign-based inter-node communication
- In-memory storage (ConcurrentHashMap)

---

## Architecture
The node which receives the request from the Client acts as the Coordinator node.       
(Here it's **Node A**)

```mermaid
graph TD
    Client --> NodeA
    NodeA -->|"hash(key)"| HashRing
    HashRing --> NodeB
    HashRing --> NodeC
    NodeA --> NodeB
    NodeA --> NodeC
```

---

## Hash Ring Logic

```mermaid
graph LR
    A[Key Hash] --> B{tailMap empty?}
    B -- Yes --> C["firstKey()"]
    B -- No --> D["tail.firstKey()"]
```

---

## Write Flow

```mermaid
sequenceDiagram
    participant Client
    participant NodeA
    participant NodeB
    participant NodeC

    Client->>NodeA: PUT(key, value)
    NodeA->>NodeA: compute replicas
    NodeA->>NodeB: replicate
    NodeA->>NodeC: replicate
    NodeA->>NodeA: local write
```
---
## Read Flow

```mermaid
sequenceDiagram
    participant Client
    participant NodeA as Current Node
    participant HashRing
    participant PrimaryNode

    Client->>NodeA: GET /get/{key}

    NodeA->>HashRing: getPrimary(key)
    HashRing-->>NodeA: Primary Node

    alt Primary is current node
        NodeA-->>Client: return value
    else Primary is another node
        NodeA->>PrimaryNode: Feign GET /get/{key}
        PrimaryNode-->>NodeA: value
        NodeA-->>Client: return value
    end
```

---

## Core Code Snippets

### Get Primary Node

```java
int nodeHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
```

### Replication Loop

```java
for (Node node : replicas) {
    if (node.getId().equals(selfId)) {
        store.put(key, value);
    } else {
        feignClientMap.get(node.getId()).replicate(req);
    }
}
```

---

## Running Multiple Nodes (Manually)

### Node A
```bash
SERVER_PORT=8081 NODES=nodeA:http://localhost:8081,nodeB:http://localhost:8082,nodeC:http://localhost:8083 SELF_NAME=nodeA java -jar build/libs/hashandra-node-1.0.jar
```

### Node B
```bash
SERVER_PORT=8081 NODES=nodeA:http://localhost:8081,nodeB:http://localhost:8082,nodeC:http://localhost:8083 SELF_NAME=nodeB java -jar build/libs/hashandra-node-1.0.jar
```

### Node C
```bash
SERVER_PORT=8081 NODES=nodeA:http://localhost:8081,nodeB:http://localhost:8082,nodeC:http://localhost:8083 SELF_NAME=nodeC java -jar build/libs/hashandra-node-1.0.jar
```

## Run Using Docker Compose

- Make sure you're in the same directory as the [docker-compose.yaml](docker-compose.yaml) file
- Add/remove nodes if you want. Do it in the [docker-compose.yaml](docker-compose.yaml) file
- Run using:
```bash
docker compose up
```
**Note**: A working image for this application - ```sumanthgma4/hashandra-node:1.0```, is already present in Docker Hub. So this yaml file will pull it and run it automatically.

---

## Testing

```bash
curl -X POST http://localhost:8081/put \
     -H "Content-Type: application/json" \
     -d '{"key":"user1","value":"Sumanth"}'
```

---

## Key Learnings

- Consistent hashing distributes keys deterministically
- Coordinator node handles routing
- Replication ensures redundancy
- Each node maintains identical hash ring

---

## Future Improvements

- Quorum reads/writes
- Health checks via heartbeats
- Eventual consistency
- Read repairs
- Hinted handoff
- Node failure handling
- Gossip protocol
- Virtual nodes