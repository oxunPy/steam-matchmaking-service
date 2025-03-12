package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.NoteData;
import org.example.steammatchmakingservice.service.MatchmakingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
public class KafkaMatchmakingConsumer {
    private final MatchmakingService matchmakingService;

    public KafkaMatchmakingConsumer(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @KafkaListener(topics = {"${kafka.topic.request.matchmaking}"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryMatchmakingRequest")
    public void listenMatchmakingRequests(MatchmakingRequestDto request) {
        matchmakingService.processMatchmaking(request)
                .then(matchmakingService.handleSteamMatch(request))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> System.out.println("Processed: " + request))
                .doOnError(error -> System.out.println("Error request: " + request))
                .subscribe();
    }

    @KafkaListener(topics = {"kafka.topic.request.notification"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryNoteData")
    public void listenNoteDataFromUser(NoteData note) {
        System.out.println(note);
    }
}

