package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.service.MatchmakingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
public class MatchmakingConsumerService {
    private final MatchmakingService matchmakingService;

    public MatchmakingConsumerService(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @KafkaListener(topics = {"${kafka.topic.request.matchmaking}"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryMatchmakingRequest")
    public void listenMatchmakingRequests(MatchmakingRequestDto request) {
        matchmakingService.processMatchmaking(request)
                .then(matchmakingService.handleSteamMatch(request))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> System.out.println("Processed: " + request))
                .subscribe();
    }
}

