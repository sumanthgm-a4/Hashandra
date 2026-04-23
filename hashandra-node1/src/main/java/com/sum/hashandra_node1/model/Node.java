package com.sum.hashandra_node1.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Node {
    String id;
    String url;
}
