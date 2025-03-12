package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.NoteData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class KafkaMatchmakingProducer {

    @Value("${kafka.topic.request.matchmaking}")
    private String matchmakingRequestTopic;
    @Value("${kafka.topic.request.notification}")
    private String matchmakingRequestNotification;

    private final KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplateMatchRequest;
    private final KafkaTemplate<String, NoteData> kafkaTemplateNoteData;

    public KafkaMatchmakingProducer(KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplateMatchmaking,
                                    KafkaTemplate<String, NoteData> kafkaTemplateNoteData) {
        this.kafkaTemplateMatchRequest = kafkaTemplateMatchmaking;
        this.kafkaTemplateNoteData = kafkaTemplateNoteData;
    }

    public Mono<Void> sendMatchmakingRequest(MatchmakingRequestDto request) {
        return Mono.fromFuture(kafkaTemplateMatchRequest.send(matchmakingRequestTopic, request.requestId(), request)).then();
    }

    public Mono<Void> sendMatchmakingNotification(NoteData noteData) {
        return Mono.fromFuture(kafkaTemplateNoteData.send(matchmakingRequestNotification, UUID.randomUUID().toString(), noteData))
                .doOnSuccess(result -> System.out.println("✅ Sent note-data to the Players in the game by Kafka: " + noteData.username()))
                .doOnError(error -> System.err.println("❌ Kafka send failed: " + error.getMessage()))
                .then();
    }
}
