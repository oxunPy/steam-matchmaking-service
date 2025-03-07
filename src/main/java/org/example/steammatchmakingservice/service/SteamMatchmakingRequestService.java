package org.example.steammatchmakingservice.service;

import org.example.steammatchmakingservice.dto.SteamMatchmakingRequestDto;
import org.example.steammatchmakingservice.entity.SteamBaseEntity;
import org.example.steammatchmakingservice.entity.SteamMatchmakingRequest;
import org.example.steammatchmakingservice.repository.MatchmakingRequestR2dbcRepository;
import org.example.steammatchmakingservice.repository.MatchmakingSessionR2dbcRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SteamMatchmakingRequestService {
    private final MatchmakingRequestR2dbcRepository matchmakingR2dbcRepository;
    private final MatchmakingRequestR2dbcRepository matchmakingRequestR2dbcRepository;

    public SteamMatchmakingRequestService(MatchmakingRequestR2dbcRepository matchmakingR2dbcRepository, MatchmakingRequestR2dbcRepository matchmakingRequestR2dbcRepository) {
        this.matchmakingR2dbcRepository = matchmakingR2dbcRepository;
        this.matchmakingRequestR2dbcRepository = matchmakingRequestR2dbcRepository;
    }

    public Mono<SteamMatchmakingRequestDto> createRequest(SteamMatchmakingRequestDto dto) {
        SteamMatchmakingRequest newRequest = new SteamMatchmakingRequest();
        newRequest.setPlayers(dto.getPlayers());
        newRequest.setSessionId(dto.getSessionId());
        newRequest.setStatus(SteamBaseEntity.SessionStatus.OPEN);
        newRequest.setCreatedAt(LocalDateTime.now());

        return matchmakingR2dbcRepository.save(newRequest)
                .map(newEntity -> {
                    return newEntity.toDto();
                });
    }

    public Mono<SteamMatchmakingRequestDto> updateRequest(String requestId, SteamMatchmakingRequestDto dto) {
        SteamMatchmakingRequest newRequest = new SteamMatchmakingRequest();
        newRequest.setPlayers(dto.getPlayers());
        newRequest.setStatus(SteamBaseEntity.SessionStatus.OPEN);
        newRequest.setCreatedAt(LocalDateTime.now());
        newRequest.setId(UUID.fromString(requestId));

        return matchmakingR2dbcRepository.updatePlayersById(newRequest.getPlayers(), requestId, dto.getSessionId())
                .thenReturn(dto);
    }

    public Mono<Void> deleteRequest(String requestId) {
        return matchmakingR2dbcRepository.deleteById(UUID.fromString(requestId));
    }

    public Flux<SteamMatchmakingRequestDto> listAllRequests() {
        return matchmakingRequestR2dbcRepository.all()
                .map(entity -> entity.toDto());
    }
}
