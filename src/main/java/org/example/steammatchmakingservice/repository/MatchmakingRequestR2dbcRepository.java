package org.example.steammatchmakingservice.repository;

import org.example.steammatchmakingservice.entity.SteamMatchmakingRequest;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MatchmakingRequestR2dbcRepository extends R2dbcRepository<SteamMatchmakingRequest, UUID> {

    @Query(value = "SELECT * FROM matchmaking_requests ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    Flux<SteamMatchmakingRequest> findAvailableMatches(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "UPDATE matchmaking_requests SET players = :players, session_id = :session_id WHERE cast(id as varchar) = :request_id")
    Mono<Void> updatePlayersById(@Param("players") List<String> players,
                                 @Param("request_id") String requestId,
                                 @Param("session_id") String sessionId);

    @Query(value = "SELECT * FROM matchmaking_requests")
    Flux<SteamMatchmakingRequest> all();
}
