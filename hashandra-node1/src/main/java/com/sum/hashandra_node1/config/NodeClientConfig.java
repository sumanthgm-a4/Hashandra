package com.sum.hashandra_node1.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sum.hashandra_node1.model.Node;
import com.sum.hashandra_node1.model.NodeClient;

import feign.Feign;

@Configuration
public class NodeClientConfig {

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

    @Bean("feignClientMap")
    public Map<String, NodeClient> feignClientMap() {
        Map<String, NodeClient> map = new HashMap<>();

        List<Node> nodes = List.of(
            Node.builder()
                    .id(node1Name)
                    .url(node1Url)
                .build(),
            Node.builder()
                    .id(node2Name)
                    .url(node2Url)
                .build(),
            Node.builder()
                    .id(node3Name)
                    .url(node3Url)
                .build()
        );

        for (Node node : nodes) {
            NodeClient client = Feign.builder()
                .target(NodeClient.class, node.getUrl());

            map.put(node.getId(), client);
        }

        return map;
    }
}
