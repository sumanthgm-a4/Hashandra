package com.sum.hashandra_node1.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sum.hashandra_node1.model.Node;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashRingService {

    public final List<Node> availableNodes;

    public final TreeMap<Integer, Node> hashRing = new TreeMap<>();
    
    public void addNode(Node node) {
        int hash = hash(node.getId());
        hashRing.putIfAbsent(hash, node);
    }

    @PostConstruct
    public void init() {
        availableNodes.stream()
            .forEach(this::addNode);
    }

    public Node getPrimary(String key) {
        int hash = hash(key);
        var tail = hashRing.tailMap(hash);
        int nodeHash = tail.isEmpty() ? hashRing.firstKey() : tail.firstKey();
        return hashRing.get(nodeHash);
    }

    public List<Node> getReplicas(String key, Integer replicationFactor) {
        List<Node> replicas = new ArrayList<>();
        int hash = hash(key);

        SortedMap<Integer, Node> tailMap = hashRing.tailMap(hash);

        Iterator<Node> it = Stream.concat(
            tailMap.values().stream(),
            hashRing.values().stream()
        ).iterator();

        while (replicas.size() < replicationFactor && it.hasNext()) {
            replicas.add(it.next());
        }

        return replicas;
    }

    private Integer hash(String nodeId) {
        // Return a normalized hash value
        // String's .hashCode ranges from -2^31 to 2^31 - 1
        // Returning a hash between 0 to 2^31 - 1
        return nodeId.hashCode() & 0x7fffffff;
    }
}
