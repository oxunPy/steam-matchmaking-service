package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.AcceptInvitation;
import org.example.steammatchmakingservice.game.InvitationFriend;
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
    @Value("${kafka.topic.request.invite-friend}")
    private String inviteFriendRequestTopic;
    @Value("${kafka.topic.request.accept-invite}")
    private String acceptInviteRequestTopic;

    private final KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplateMatchRequest;
    private final KafkaTemplate<String, NoteData> kafkaTemplateNoteData;
    private final KafkaTemplate<String, InvitationFriend> kafkaTemplateInvitation;
    private final KafkaTemplate<String, AcceptInvitation> kafkaTemplateAcceptInvitation;

    public KafkaMatchmakingProducer(KafkaTemplate<String, MatchmakingRequestDto> kafkaTemplateMatchmaking,
                                    KafkaTemplate<String, NoteData> kafkaTemplateNoteData,
                                    KafkaTemplate<String, InvitationFriend> kafkaTemplateInvitation,
                                    KafkaTemplate<String, AcceptInvitation> kafkaTemplateAcceptInvitation) {
        this.kafkaTemplateMatchRequest = kafkaTemplateMatchmaking;
        this.kafkaTemplateNoteData = kafkaTemplateNoteData;
        this.kafkaTemplateInvitation = kafkaTemplateInvitation;
        this.kafkaTemplateAcceptInvitation = kafkaTemplateAcceptInvitation;
    }

    public Mono<Void> sendMatchmakingRequest(MatchmakingRequestDto request) {
        return Mono.fromFuture(kafkaTemplateMatchRequest.send(matchmakingRequestTopic, request.requestId(), request))
                .doOnSuccess(result -> System.out.println("✅ Sent matchmaking-request to the Players in the game by Kafka: " + request.requestId()))
                .doOnError(error -> System.err.println("❌ Kafka send failed(MatchmakingRequest): " + error.getMessage()))
                .then();
    }

    public Mono<Void> sendMatchmakingNotification(NoteData noteData) {
        return Mono.fromFuture(kafkaTemplateNoteData.send(matchmakingRequestNotification, UUID.randomUUID().toString(), noteData))
                .doOnSuccess(result -> System.out.println("✅ Sent note-data to the Players in the game by Kafka: " + noteData.sender()))
                .doOnError(error -> System.err.println("❌ Kafka send failed(NoteData): " + error.getMessage()))
                .then();
    }

    public Mono<Void> sendFriendInvitation(InvitationFriend inviteReq) {
        return Mono.fromFuture(kafkaTemplateInvitation.send(inviteFriendRequestTopic, UUID.randomUUID().toString(), inviteReq))
                .doOnSuccess(result -> System.out.println("✅ Sent inviteReq to the Player in the game by Kafka: " + inviteReq.senderUsername()))
                .doOnError(error -> System.err.println("❌ Kafka send failed(InvitationFriend): " + error.getMessage()))
                .then();
    }

    public Mono<Void> sendAcceptInvitation(AcceptInvitation accInvite) {
        return Mono.fromFuture(kafkaTemplateAcceptInvitation.send(acceptInviteRequestTopic, UUID.randomUUID().toString(), accInvite))
                .doOnSuccess(result -> System.out.println("✅ Sent accept-invite to the Players in the game by Kafka: " + accInvite.senderUsername()))
                .doOnError(error -> System.err.println("❌ Kafka send failed(AcceptInvitation): " + error.getMessage()))
                .then();
    }
}
