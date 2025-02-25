package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.example.steammatchmakingservice.service.MatchmakingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MatchmakingConsumerService {
    private final MatchmakingService matchmakingService;

    public MatchmakingConsumerService(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @KafkaListener(topics = {"${kafka.topic.request.matchmaking}"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryString")
    public void listenMatchmakingRequests(MatchmakingRequest request) {
        matchmakingService.processMatchmaking(request)
                .doOnSuccess(result -> System.out.println("Processed: " + request))
                .subscribe();
    }

}

