package org.example.steammatchmakingservice.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static org.example.steammatchmakingservice.redis.ReactiveRedisPublisher.ACCEPT_INVITE_MSG_STREAM;
import static org.example.steammatchmakingservice.redis.ReactiveRedisPublisher.INVITE_MSG_STREAM;

@Component
public class ReactiveRedisSubscriber {

    private final StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;
    private final RedisSocketSessionService redisSocketSessionService;

    public ReactiveRedisSubscriber(ReactiveRedisConnectionFactory factory,
                                   RedisSocketSessionService redisSocketSessionService) {
        this.streamReceiver = StreamReceiver.create(factory);
        this.redisSocketSessionService = redisSocketSessionService;
    }

    public void startListeningInviteMsg() {
        streamReceiver.receive(StreamOffset.fromStart(INVITE_MSG_STREAM))
                .flatMap(record -> {
                    String senderUsername = record.getValue().get("senderUsername");
                    String receiverUsername = record.getValue().get("receiverUsername");

                    return Mono.defer(() -> {
                        Sinks.Many<String> friendSink = redisSocketSessionService.getUserSink(receiverUsername);
                        if(friendSink != null) {
                            friendSink.tryEmitNext(senderUsername + " has invited you to play matchmaking!");
                        }
                        return Mono.empty();
                    });
                })
                .onErrorContinue((ex, obj) -> System.out.println("Error processing message: " + ex.getMessage()))
                .subscribe();
    }

    public void startListeningAcceptInviteMsg() {
        streamReceiver.receive(StreamOffset.fromStart(ACCEPT_INVITE_MSG_STREAM))
                .flatMap(record -> {
                    String senderUsername = record.getValue().get("senderUsername");
                    boolean accepted = Boolean.parseBoolean(record.getValue().get("accepted"));

                    return Mono.defer(() -> {
                        Sinks.Many<String> friendSink = redisSocketSessionService.getUserSink(senderUsername);
                        if(friendSink != null) {
                            friendSink.tryEmitNext(accepted ? "Your friend has accepted the invitation!" : "Not accepted!");
                        }
                        return Mono.empty();
                    });
                })
                .onErrorContinue((ex, obj) -> System.out.println("Error processing message: " + ex.getMessage()))
                .subscribe();
    }
}
