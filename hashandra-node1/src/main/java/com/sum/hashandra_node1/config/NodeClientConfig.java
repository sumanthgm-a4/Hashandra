package com.sum.hashandra_node1.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverters;

import com.sum.hashandra_node1.model.Node;
import com.sum.hashandra_node1.model.NodeClient;

import feign.Feign;
import feign.codec.StringDecoder;

@Configuration
public class NodeClientConfig {

    @Value("${nodeConfigs.nodes}")
    private String nodesConfig;

    @Bean("feignClientMap")
    public Map<String, NodeClient> feignClientMap(ObjectFactory<FeignHttpMessageConverters> converters) {
        Map<String, NodeClient> map = new HashMap<>();

        List<Node> nodes = getNodes();

        for (Node node : nodes) {
            NodeClient client = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                // .decoder(new StringDecoder())
                .contract(new SpringMvcContract())
                .target(NodeClient.class, node.getUrl());

            map.put(node.getId(), client);
        }

        return map;
    }

    @Bean("availableNodes")
    public List<Node> getNodes() {
        return Arrays.stream(nodesConfig.split(","))
                .map(entry -> {
                    String[] parts = entry.split(":", 2);

                    return Node.builder()
                            .id(parts[0])
                            .url(parts[1])
                            .build();
                })
                .toList();
    }
}
