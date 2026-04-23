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

@Service
public class HashRingService {

    @Value("${nodeConfigs.urls.node1}")
    private String node1Url;
    @Value("${nodeConfigs.names.node1}")
    private String node1Name;

    @Value("${nodeConfigs.urls.node2}")
    private String node2Url;
    @Value("${nodeConfigs.names.node2}")
    private String node2Name;

    @Value("${nodeConfigs.urls.node3}")
    private String node3Url;
    @Value("${nodeConfigs.names.node3}")
    private String node3Name;

    public final TreeMap<Integer, Node> hashRing = new TreeMap<>();
    
    public void addNode(Node node) {
        int hash = hash(node.getId());
        hashRing.putIfAbsent(hash, node);
    }

    @PostConstruct
    public void init() {
        addNode(Node.builder()
                    .id(node1Name)
                    .url(node1Url)
                .build());
        addNode(Node.builder()
                    .id(node2Name)
                    .url(node2Url)
                .build());
        addNode(Node.builder()
                    .id(node3Name)
                    .url(node3Url)
                .build());
    }

    public Node getPrimary(String key) {
        int hash = hash(key);
        var tail = hashRing.tailMap(hash);
        int nodeHash = tail.isEmpty() ? hashRing.firstKey() : tail.firstKey();
        return hashRing.get(nodeHash);
    }

    List<Node> getReplicas(String key, Integer replicationFactor) {
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
