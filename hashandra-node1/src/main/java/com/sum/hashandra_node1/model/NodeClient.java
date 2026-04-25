package com.sum.hashandra_node1.model;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// A Generic Feign Client

// @FeignClient(name = "node-client", url = "${node.url}")
public interface NodeClient {

    @PostMapping(value = "/replicate", consumes = "application/json")
    void replicate(@RequestBody KVRequest req);

    @GetMapping(value = "/get/{key}", produces = "application/json")
    KVResponse getValue(@PathVariable String key);
}