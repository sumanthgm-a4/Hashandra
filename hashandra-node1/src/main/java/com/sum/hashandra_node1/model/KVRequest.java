package com.sum.hashandra_node1.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class KVRequest {

    String key;
    String value;
}
