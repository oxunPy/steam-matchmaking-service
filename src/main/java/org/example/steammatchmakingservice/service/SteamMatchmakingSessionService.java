package org.example.steammatchmakingservice.service;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.dto.SteamMatchmakingSessionDto;
import org.example.steammatchmakingservice.entity.SteamBaseEntity;
import org.example.steammatchmakingservice.entity.SteamMatchmakingSession;
import org.example.steammatchmakingservice.repository.MatchmakingSessionR2dbcRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class SteamMatchmakingSessionService {
    private final MatchmakingSessionR2dbcRepository matchmakingSessionR2dbcRepository;
    public SteamMatchmakingSessionService(MatchmakingSessionR2dbcRepository matchmakingSessionR2dbcRepository) {
        this.matchmakingSessionR2dbcRepository = matchmakingSessionR2dbcRepository;
    }

    public Mono<SteamMatchmakingSessionDto> create(SteamMatchmakingSessionDto dto) {
        SteamMatchmakingSession newSession = new SteamMatchmakingSession();
        BeanUtils.copyProperties(dto, newSession, "players");
        newSession.setPlayers(dto.getPlayers());
        newSession.setStatus(SteamBaseEntity.SessionStatus.OPEN);

        return matchmakingSessionR2dbcRepository.save(newSession)
                .map(entity -> entity.toDto());
    }

    public Mono<SteamMatchmakingSessionDto> update(String sessionId, SteamMatchmakingSessionDto dto) {
        return matchmakingSessionR2dbcRepository.updateSession(
                sessionId,
                dto.getOpenSlots(),
                dto.getPlayers(),
                dto.getTotalPlayers(),
                SteamBaseEntity.SessionStatus.PLAYING_GAME.ordinal()
                )
                .thenReturn(dto);
    }

    public Mono<Void> delete(String sessionId) {
        return matchmakingSessionR2dbcRepository.deleteById(UUID.fromString(sessionId));
    }

    public Flux<SteamMatchmakingSessionDto> listAllSessions() {
        return matchmakingSessionR2dbcRepository.all()
                .map(entity -> entity.toDto());
    }

    public Mono<SteamMatchmakingSession> findFirstAvailableMatchSessionOrCreate(MatchmakingRequestDto request) {
        return matchmakingSessionR2dbcRepository.findFirstAvailableMatchSession(request.maxPlayers())
                .switchIfEmpty(Mono.defer(() -> {
                    SteamMatchmakingSession newSession = SteamMatchmakingSession.initNew(request.gameMode());
                    newSession.setOpenSlots(request.gameMode().maxPlayers());
                    return matchmakingSessionR2dbcRepository.save(newSession);
                }));
    }

    public Mono<SteamMatchmakingSession> findMatchSessionByJoinRequestId(String requestId) {
        return matchmakingSessionR2dbcRepository.findMatchSessionByJoinRequestId(requestId);
    }
}