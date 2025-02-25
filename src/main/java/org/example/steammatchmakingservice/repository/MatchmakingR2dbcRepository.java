package org.example.steammatchmakingservice.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.steammatchmakingservice.entity.SteamMatchmakingRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MatchmakingR2dbcRepository extends ReactiveCrudRepository<SteamMatchmakingRequest, Long> {
    @Query(value = "SELECT * FROM matchmaking_requests ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    Flux<SteamMatchmakingRequest> findAvailableMatches(@Param("limit") int limit, @Param("offset") int offset);
}
