package org.example.steammatchmakingservice.redis;

import org.example.steammatchmakingservice.game.AcceptInvitation;
import org.example.steammatchmakingservice.game.InvitationFriend;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ReactiveRedisPublisher {
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    static final String INVITE_MSG_STREAM = "invite-friend-messages";
    static final String ACCEPT_INVITE_MSG_STREAM = "accept-invite-messages";
    static final String NOTE_DATA_MSG_STREAM = "note-data-messages";

    public ReactiveRedisPublisher(@Qualifier("reactiveRedisTemplateObj") ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Void> sendInviteMsgStream(InvitationFriend invFriend) {
        return redisTemplate.opsForStream()
                .add(ObjectRecord.create(INVITE_MSG_STREAM, Map.of(
                        "senderUsername", invFriend.senderUsername(),
                        "receiverUsername", invFriend.receiverUsername()
                )))
                .doOnSuccess(recordId -> System.out.println("✅ Message published to Redis Stream: " + recordId))
                .doOnError(error -> System.err.println("❌ Redis Stream publish error: " + error.getMessage()))
                .then();
    }

    public Mono<Void> sendAcceptInvitationMsgStream(AcceptInvitation accInv) {
        return redisTemplate.opsForStream()
                .add(ObjectRecord.create(ACCEPT_INVITE_MSG_STREAM, Map.of(
                    "senderUsername", accInv.senderUsername(),
                    "accepted",  String.valueOf(accInv.accepted())
                )))
                .doOnSuccess(recordId -> System.out.println("✅ Message published to Redis Stream: " + recordId))
                .doOnError(error -> System.err.println("❌ Redis Stream publish error: " + error.getMessage()))
                .then();
    }

    public Mono<Void> sendNoteDataMsgStream() {
        return Mono.empty();
    }
}
