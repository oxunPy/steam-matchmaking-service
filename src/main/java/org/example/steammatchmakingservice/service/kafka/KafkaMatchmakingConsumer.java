package org.example.steammatchmakingservice.service.kafka;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.AcceptInvitation;
import org.example.steammatchmakingservice.game.InvitationFriend;
import org.example.steammatchmakingservice.game.NoteData;
import org.example.steammatchmakingservice.redis.ReactiveRedisPublisher;
import org.example.steammatchmakingservice.service.MatchmakingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
public class KafkaMatchmakingConsumer {
    private final MatchmakingService matchmakingService;
    private final ReactiveRedisPublisher redisPublisher;

    public KafkaMatchmakingConsumer(MatchmakingService matchmakingService, ReactiveRedisPublisher redisPublisher) {
        this.matchmakingService = matchmakingService;
        this.redisPublisher = redisPublisher;
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


    @KafkaListener(topics = {"${kafka.topic.request.invite-friend}"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryInvitationFriend")
    public void listenFriendInvitation(InvitationFriend invitationReq) {
        System.out.println(invitationReq);
        redisPublisher.sendInviteMsgStream(invitationReq)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> System.out.println("✅ Sent to Redis: "))
                .doOnError(error ->  System.err.println("❌ Redis send failed: " + error.getMessage()))
                .subscribe();

    }

    @KafkaListener(topics = {"${kafka.topic.request.accept-invite}"}, groupId = "${kafka.consumer.matchmaking.group_id}", containerFactory = "kafkaListenerContainerFactoryAcceptInvitation")
    public void listenAcceptInvitation(AcceptInvitation acceptInvitation) {
        redisPublisher.sendAcceptInvitationMsgStream(acceptInvitation)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> System.out.println("✅ Sent to Redis: "))
                .doOnError(error ->  System.err.println("❌ Redis send failed: " + error.getMessage()))
                .subscribe();
    }
}

