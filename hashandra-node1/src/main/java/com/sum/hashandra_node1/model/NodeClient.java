package com.sum.hashandra_node1.model;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// A Generic Feign Client

// @FeignClient(name = "node-client", url = "${node.url}")
public interface NodeClient {

    @PostMapping("/replicate")
    void replicate(KVRequest req);

    @GetMapping("/internal/get/{key}")
    String getValue(@PathVariable String key);
}