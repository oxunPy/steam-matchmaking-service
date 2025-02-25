package org.example.steammatchmakingservice.config.kafka.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.example.steammatchmakingservice.dto.MatchmakingRequest;

public class MatchmakingRequestSerializer implements Serializer<MatchmakingRequest> {

    private final ObjectMapper objMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, MatchmakingRequest data) {
        try {
            return objMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing Matchmaking request");
        }
    }
}
