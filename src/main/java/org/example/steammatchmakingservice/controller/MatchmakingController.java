package org.example.steammatchmakingservice.controller;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.game.GameMode;
import org.example.steammatchmakingservice.response.MatchmakingResponse;
import org.example.steammatchmakingservice.service.MatchmakingService;
import org.example.steammatchmakingservice.service.RedisSocketSessionService;
import org.example.steammatchmakingservice.service.kafka.KafkaMatchmakingProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/steam-matchmaking")
public class MatchmakingController {
    private final KafkaMatchmakingProducer matchmakingProducer;
    private final MatchmakingService matchmakingService;
    private final RedisSocketSessionService redisSocketSessionService;

    public MatchmakingController(KafkaMatchmakingProducer matchmakingProducer, MatchmakingService matchmakingService, RedisSocketSessionService redisSocketSessionService) {
        this.matchmakingProducer = matchmakingProducer;
        this.matchmakingService = matchmakingService;
        this.redisSocketSessionService = redisSocketSessionService;
    }

    @PostMapping("/request-deathmatch")
    public Mono<Void> requestMatchmakingDmatch(@RequestBody MatchmakingRequestDto payload) {
        int maxPlayers = 1 + payload.friends().size();
        if(maxPlayers > 10) {
            return Mono.create(t -> MatchmakingResponse.builder()
                    .status(MatchmakingResponse.Status.FAILED)
                    .message("Max number of players is 10").build());
        }

        MatchmakingRequestDto request = new MatchmakingRequestDto(UUID.randomUUID().toString(), payload.mainPlayer(), GameMode.DEATHMATCH, payload.friends(), maxPlayers);
        return matchmakingProducer.sendMatchmakingRequest(request);
    }

    @PostMapping("/request-competetive")
    public Mono<Void> requestMatchmakingComp(@RequestBody MatchmakingRequestDto payload) {
        int maxPlayers = 1 + payload.friends().size();

        if(maxPlayers > 5) {
            return Mono.create(t -> MatchmakingResponse.builder()
                    .status(MatchmakingResponse.Status.FAILED)
                    .message("Max number of players is 5").build());
        }

        MatchmakingRequestDto request = new MatchmakingRequestDto(UUID.randomUUID().toString(), payload.mainPlayer(), GameMode.COMPETETIVE, payload.friends(), maxPlayers);
        return matchmakingProducer.sendMatchmakingRequest(request);
    }


    @PostMapping("/save-session")
    public Mono<MatchmakingResponse> saveMatchSession(@RequestBody MatchmakingRequestDto payload) {

        int maxPlayers = 1 + payload.friends().size();
        MatchmakingRequestDto request = new MatchmakingRequestDto(UUID.randomUUID().toString(), payload.mainPlayer(), GameMode.COMPETETIVE, payload.friends(), maxPlayers);
        return matchmakingService.createNewMatchmakingSession(request);
    }

    @GetMapping("/health/connections")
    public ResponseEntity<Integer> getActiveConnections() {
        return ResponseEntity.ok(redisSocketSessionService.getActiveConnections());
    }
}
