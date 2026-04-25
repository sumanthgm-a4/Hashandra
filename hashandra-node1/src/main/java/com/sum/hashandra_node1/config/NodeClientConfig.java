package com.sum.hashandra_node1.config;

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

    // Don't need Feign call to self, lol
    // @Value("${nodeConfigs.urls.self}")
    // private String node1Url;
    // @Value("${nodeConfigs.names.self}")
    // private String node1Name;

    @Value("${nodeConfigs.urls.node2}")
    private String node2Url;
    @Value("${nodeConfigs.names.node2}")
    private String node2Name;

    @Value("${nodeConfigs.urls.node3}")
    private String node3Url;
    @Value("${nodeConfigs.names.node3}")
    private String node3Name;

    @Bean("feignClientMap")
    public Map<String, NodeClient> feignClientMap(ObjectFactory<FeignHttpMessageConverters> converters) {
        Map<String, NodeClient> map = new HashMap<>();

        List<Node> nodes = List.of(
            // Node.builder()
            //         .id(node1Name)
            //         .url(node1Url)
            //     .build(),
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
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                // .decoder(new StringDecoder())
                .contract(new SpringMvcContract())
                .target(NodeClient.class, node.getUrl());

            map.put(node.getId(), client);
        }

        return map;
    }
}
