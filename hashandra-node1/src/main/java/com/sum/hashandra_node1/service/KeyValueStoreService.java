package com.sum.hashandra_node1.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.sum.hashandra_node1.model.KVResponse;

@Service
public class KeyValueStoreService {

    ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        store.put(key, value);
        System.out.println("\nKey-Value Store: " + store + "\n");
    }

    public KVResponse get(String key) {
        return new KVResponse(key, store.get(key));
    }
}
