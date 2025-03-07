package org.example.steammatchmakingservice.repository;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.dto.StoredMatchRequestDto;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MatchmakingRedisRepository {
    private static final String MATCHMAKING_KEY_PREFIX = "matchmaking:";
    private final ReactiveRedisTemplate<String, StoredMatchRequestDto> redisTemplate;
    private final ReactiveRedisTemplate<String, Object> redisTemplateObj;

    public MatchmakingRedisRepository(ReactiveRedisTemplate<String, StoredMatchRequestDto> redisTemplate, ReactiveRedisTemplate<String, Object> redisTemplateObj) {
        this.redisTemplate = redisTemplate;
        this.redisTemplateObj = redisTemplateObj;
    }
}
