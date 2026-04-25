package com.sum.hashandra_node1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
public class KVResponse {

    String key;
    String value;

    @JsonCreator
    public KVResponse(
        @JsonProperty("key") String key,
        @JsonProperty("value") String value
    ) {
        this.key = key;
        this.value = value;
    }
}
