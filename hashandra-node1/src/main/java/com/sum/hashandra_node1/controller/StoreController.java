package com.sum.hashandra_node1.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sum.hashandra_node1.model.KVRequest;
import com.sum.hashandra_node1.model.KVResponse;
import com.sum.hashandra_node1.model.Node;
import com.sum.hashandra_node1.model.NodeClient;
import com.sum.hashandra_node1.service.HashRingService;
import com.sum.hashandra_node1.service.KeyValueStoreService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StoreController {

    @Value("${nodeConfigs.self}")
    private String selfId;

    private final HashRingService hashRingService;
    private final KeyValueStoreService keyValueStoreService;

    private final Map<String, NodeClient> feignClientMap;

    @PostMapping(value = "/put", consumes = "application/json")
    public ResponseEntity<String> put(@RequestBody KVRequest request) {
        List<Node> replicas = hashRingService.getReplicas(request.getKey(), 3);

        for (Node node : replicas) {
            if (node.getId().equals(selfId)) {
                keyValueStoreService.put(request.getKey(), request.getValue());
            } else {
                feignClientMap.get(node.getId()).replicate(request);
            }
        }

        return ResponseEntity.ok("Stored");
    }

    @GetMapping(value = "/get/{key}", produces = "application/json")
    public ResponseEntity<KVResponse> get(@PathVariable String key) {
        Node primary = hashRingService.getPrimary(key);

        KVResponse response;
        if (primary.getId().equals(selfId)) {
            response = keyValueStoreService.get(key);
        } else {
            response = feignClientMap.get(primary.getId()).getValue(key);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/replicate", consumes = "application/json")
    public void replicate(@RequestBody KVRequest request) {
        keyValueStoreService.put(request.getKey(), request.getValue());
    }
}
