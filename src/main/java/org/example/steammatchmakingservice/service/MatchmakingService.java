package org.example.steammatchmakingservice.service;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.example.steammatchmakingservice.dto.MatchmakingResponse;
import org.example.steammatchmakingservice.repository.MatchmakingRedisRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MatchmakingService {
    private final MatchmakingRedisRepository matchmakingRedisRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MatchmakingService(MatchmakingRedisRepository matchmakingRedisRepository, SimpMessagingTemplate messagingTemplate) {
        this.matchmakingRedisRepository = matchmakingRedisRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Mono<Void> processMatchmaking(MatchmakingRequest request) {
        return matchmakingRedisRepository.findExistingMatch(request)
                .flatMap(existingMatch -> Mono.empty())
                .switchIfEmpty(matchmakingRedisRepository.saveNewMatch(request))
                .then();
    }

    public void notifyMatchmakingUpdate(MatchmakingResponse response) {
        messagingTemplate.convertAndSend("/topic/matchmaking-updates", response);
    }
}
