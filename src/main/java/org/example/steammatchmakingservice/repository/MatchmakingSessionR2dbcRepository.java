package org.example.steammatchmakingservice.repository;

import org.example.steammatchmakingservice.entity.SteamMatchmakingSession;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface MatchmakingSessionR2dbcRepository extends R2dbcRepository<SteamMatchmakingSession, UUID> {

    @Query(value = "SELECT * FROM matchmaking_sessions WHERE open_slots >= :openSlots and is_closed = false order by created_at desc limit 1")
    Mono<SteamMatchmakingSession> findFirstAvailableMatchSession(int openSlots);

    @Query(value = "SELECT ms.* FROM matchmaking_sessions ms " +
                   "INNER JOIN matchmaking_requests mr on mr.session_id = cast(ms.id as varchar)" +
                   "WHERE mr.id = :request_id LIMIT 1")
    Mono<SteamMatchmakingSession> findMatchSessionByJoinRequestId(@Param("request_id") String requestId);

    @Query(value = "SELECT * from matchmaking_sessions")
    Flux<SteamMatchmakingSession> all();

    @Query(value = "UPDATE matchmaking_sessions\n" +
            "SET open_slots = :open_slots,\n" +
            "    players = :players,\n" +
            "    total_players = :total_players, \n" +
            "    status = :status\n" +
            "WHERE cast(id as varchar) = :session_id")
    Mono<Void> updateSession(@Param("session_id") String sessionId,
                             @Param("open_slots") Integer openSlots,
                             @Param("players") List<String> players,
                             @Param("total_players") Integer totalPlayers,
                             @Param("status") Integer status);
}
