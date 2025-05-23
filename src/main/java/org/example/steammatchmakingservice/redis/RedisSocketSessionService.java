package org.example.steammatchmakingservice.redis;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisSocketSessionService {

    private static final String MATCHMAKING_SESSION_PREFIX = "matchmaking:session:";
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final Map<String, Sinks.Many<String>> userSinks;

    public RedisSocketSessionService(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        userSinks = new ConcurrentHashMap<>();
    }

    // Save session for each user
    public Mono<Void> saveSession(String username, String sessionId) {
        return redisTemplate.opsForValue().
                set(MATCHMAKING_SESSION_PREFIX + username, sessionId)
                .then();
    }

    // Find session by username
    public Mono<String> findSessionId(String username) {
        return redisTemplate.opsForValue().get(MATCHMAKING_SESSION_PREFIX + username);
    }

    // Register a sink for the user to send/recieve payload
    public void registerUserSink(String username) {
        userSinks.computeIfAbsent(username.toLowerCase(), key -> Sinks.many().multicast().directBestEffort());
    }

    // Get user's sink for payload sending
    public Sinks.Many<String> getUserSink(String username) {
        return userSinks.get(username.toLowerCase());
    }

    // Send message to a user via their sink
    public Mono<Void> sendMessageToUser(String username, String message) {
        Sinks.Many<String> sink = userSinks.get(username);
        if(sink != null) {
            return Mono.fromRunnable(() -> sink.tryEmitNext(message));
        }

        return Mono.empty();
    }

    // Remove session from Redis when user disconnects
    public Mono<Void> removeSession(String username) {
        return redisTemplate.opsForValue().delete(MATCHMAKING_SESSION_PREFIX + username).then();
    }

    // Remove user sinks when user disconnects
    public Mono<Void> removeUserSink(String username) {
        return Mono.fromRunnable(() -> userSinks.remove(username));
    }

    public int getActiveConnections() {
        return userSinks.size();
    }
}
