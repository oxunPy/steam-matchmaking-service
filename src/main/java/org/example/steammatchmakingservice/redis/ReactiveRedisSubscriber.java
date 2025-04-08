package org.example.steammatchmakingservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


import java.util.HashMap;
import java.util.Map;

import static org.example.steammatchmakingservice.redis.ReactiveRedisPublisher.ACCEPT_INVITE_MSG_STREAM;
import static org.example.steammatchmakingservice.redis.ReactiveRedisPublisher.INVITE_MSG_STREAM;

@Component
public class ReactiveRedisSubscriber {

    private final StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;
    private final RedisSocketSessionService redisSocketSessionService;
    private final ObjectMapper objectMapper;

    public ReactiveRedisSubscriber(ReactiveRedisConnectionFactory factory,
                                   RedisSocketSessionService redisSocketSessionService,
                                   ObjectMapper objectMapper) {
        this.streamReceiver = StreamReceiver.create(factory);
        this.redisSocketSessionService = redisSocketSessionService;
        this.objectMapper = objectMapper;
    }

    public void startListeningInviteMsg() {
        streamReceiver.receive(StreamOffset.fromStart(INVITE_MSG_STREAM))
                .flatMap(record -> {
                    String senderUsername = record.getValue().get("senderUsername");
                    String receiverUsername = record.getValue().get("receiverUsername");

                    return Mono.defer(() -> {
                        Sinks.Many<String> friendSink = redisSocketSessionService.getUserSink(receiverUsername);
                        if(friendSink != null) {
                            Map<String, Object> jsonMap = new HashMap<>();
                            jsonMap.put("senderUsername", senderUsername);
                            jsonMap.put("actionResp", "invite");
                            jsonMap.put("msg", senderUsername + " has invited you to play matchmaking!");

                            try {
                                friendSink.tryEmitNext(objectMapper.writeValueAsString(jsonMap));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
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
                    String acceptorUsername = record.getValue().get("acceptorUsername");
                    boolean accepted = Boolean.parseBoolean(record.getValue().get("accepted"));

                    return Mono.defer(() -> {
                        Sinks.Many<String> friendSink = redisSocketSessionService.getUserSink(senderUsername);
                        if(friendSink != null) {
                            Map<String, Object> jsonMap = new HashMap<>();
                            jsonMap.put("accepted", accepted);
                            jsonMap.put("acceptorUsername", acceptorUsername);
                            jsonMap.put("actionResp", "accept");
                            jsonMap.put("msg", (accepted ? "Your friend " + acceptorUsername + " has accepted the invitation!" : "Not accepted!"));

                            try {
                                friendSink.tryEmitNext(objectMapper.writeValueAsString(jsonMap));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }

                        }
                        return Mono.empty();
                    });
                })
                .onErrorContinue((ex, obj) -> System.out.println("Error processing message: " + ex.getMessage()))
                .subscribe();
    }
}
