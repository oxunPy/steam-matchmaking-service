package org.example.steammatchmakingservice.handler;

import org.example.steammatchmakingservice.game.NoteData;
import org.example.steammatchmakingservice.service.RedisSocketSessionService;
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

        URI uri = session.getHandshakeInfo().getUri();
        System.out.println(uri.getQuery());
        System.out.println("Socket connecting.....");

        // 1.Establish connection
        if(uri.getQuery().contains("ws/match/connect")) {
            // Register user sink
            Map<String, String> queryParams = parseQueryParams(uri);
            String username = queryParams.get("username");
            redisSocketSessionService.registerUserSink(username);
        }

        // 2. Handle messages
        else {
            Map<String, String> queryParams = parseQueryParams(uri);
            String username = queryParams.get("username");
            String sessionId = queryParams.get("session");
            if(username != null && sessionId != null) {

                Flux<Void> incomingMessages = session.receive()
                        .map(socket -> socket.getPayloadAsText())
                        .flatMap(message -> {
                            if(message != null && !message.trim().isEmpty()) {
                                // send this messages to all other connected users
                                NoteData noteData = new NoteData(username, sessionId, message);
                                return kafkaProducer.sendMatchmakingNotification(noteData);
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
