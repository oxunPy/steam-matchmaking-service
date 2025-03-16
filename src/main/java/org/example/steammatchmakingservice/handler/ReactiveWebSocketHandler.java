package org.example.steammatchmakingservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.steammatchmakingservice.game.AcceptInvitation;
import org.example.steammatchmakingservice.game.InvitationFriend;
import org.example.steammatchmakingservice.game.NoteData;
import org.example.steammatchmakingservice.redis.RedisSocketSessionService;
import org.example.steammatchmakingservice.service.kafka.KafkaMatchmakingProducer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component("reactive_web_socket_handler")
public class ReactiveWebSocketHandler implements WebSocketHandler {
    private final RedisSocketSessionService redisSocketSessionService;
    private final KafkaMatchmakingProducer kafkaProducer;
    private static final int MAX_CONNECTIONS = 5000;


    public ReactiveWebSocketHandler(RedisSocketSessionService redisSocketSessionService,
                                    KafkaMatchmakingProducer kafkaProducer) {
        this.redisSocketSessionService = redisSocketSessionService;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        System.out.println("Socket connecting.....");
        URI uri = session.getHandshakeInfo().getUri();
        Map<String, String> queryParams = parseQueryParams(uri);
        String username = queryParams.get("username");
        if(username != null) {
            // Register user webSocket session as Sink
            redisSocketSessionService.registerUserSink(username);

            Flux<Void> incomingMessages = session.receive()
                    .map(socket -> socket.getPayloadAsText())
                    .flatMap(message -> {
                        if (message != null && !message.trim().isEmpty()) {
                            // send these messages to all other connected users
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(message);
                                String action = jsonNode.get("action").asText();

                                if(action.equals("invite")) {
                                    String fromPlayer = jsonNode.get("from").asText();
                                    String toPlayer = jsonNode.get("to").asText();
                                    return kafkaProducer.sendFriendInvitation(new InvitationFriend(fromPlayer, toPlayer));
                                }

                                if(action.equals("accept")) {
                                    String accPlayer = jsonNode.get("accPlayer").asText();
                                    boolean agree = jsonNode.get("agree").asBoolean();
                                    return kafkaProducer.sendAcceptInvitation(new AcceptInvitation(accPlayer, agree));
                                }

                                if(action.equals("notify")) {
                                    String fromPlayer = jsonNode.get("from").asText();
                                    String otherPlayers = jsonNode.get("otherPlayers").asText();
                                    String info = jsonNode.get("info").asText();
                                    return kafkaProducer.sendMatchmakingNotification(new NoteData(fromPlayer, Arrays.asList(otherPlayers.split(",")), info));
                                }

                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException("Invalid JSON message: " + message));
                            }
                        }

                        return Mono.empty();
                    });

            // Listen for messages sent via the Sink
            Flux<WebSocketMessage> outgoingMessages = redisSocketSessionService.getUserSink(username)
                    .asFlux()
                    .map(session::textMessage);

            return session.send(outgoingMessages)
                    .and(incomingMessages)
                    .doFinally(signalType -> {
                        redisSocketSessionService.removeSession(username).then();
                        redisSocketSessionService.removeUserSink(username);
                    });
        }

        return Mono.empty();
    }

    private Map<String, String> parseQueryParams(URI uri) {
        if(uri.getQuery() == null) new HashMap<>();
        return Arrays.stream(uri.getQuery().split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }
}
