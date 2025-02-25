package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MatchmakingProducerService {

    @Value("${kafka.topic.request.matchmaking}")
    private String matchmakingRequestTopic;

    private final KafkaTemplate<String, MatchmakingRequest> kafkaTemplate;

    public MatchmakingProducerService(KafkaTemplate<String, MatchmakingRequest> kafkaTemplateMatchmaking) {
        this.kafkaTemplate = kafkaTemplateMatchmaking;
    }

    public void sendMatchmakingRequest(MatchmakingRequest request) {
        kafkaTemplate.send(matchmakingRequestTopic, request.requestId(), request);
    }
}
