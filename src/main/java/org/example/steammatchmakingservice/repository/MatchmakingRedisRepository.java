package org.example.steammatchmakingservice.repository;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MatchmakingRedisRepository {
    private final ReactiveRedisTemplate<String, MatchmakingRequest> redisTemplate;

    public MatchmakingRedisRepository(ReactiveRedisTemplate<String, MatchmakingRequest> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<MatchmakingRequest> findExistingMatch(MatchmakingRequest request) {
        return redisTemplate.opsForValue().get("matchmaking:" + request.maxPlayers());
    }

    public Mono<Void> saveNewMatch(MatchmakingRequest request) {
        return redisTemplate.opsForValue()
                .set("matchmaking:" + request.requestId(), request)
                .then();
    }
}
