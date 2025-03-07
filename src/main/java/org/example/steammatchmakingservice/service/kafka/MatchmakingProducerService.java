package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MatchmakingProducerService {

    @Value("${kafka.topic.request.matchmaking}")
    private String matchmakingRequestTopic;

    private final KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplate;

    public MatchmakingProducerService(KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplateMatchmaking) {
        this.kafkaTemplate = kafkaTemplateMatchmaking;
    }

    public Mono<Void> sendMatchmakingRequest(MatchmakingRequestDto request) {
        return Mono.fromFuture(kafkaTemplate.send(matchmakingRequestTopic, request.requestId(), request)).then();
    }
}
