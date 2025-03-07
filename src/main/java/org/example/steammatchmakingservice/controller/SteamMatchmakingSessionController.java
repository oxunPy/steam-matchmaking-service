package org.example.steammatchmakingservice.controller;

import org.example.steammatchmakingservice.dto.SteamMatchmakingSessionDto;
import org.example.steammatchmakingservice.entity.SteamMatchmakingSession;
import org.example.steammatchmakingservice.service.SteamMatchmakingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class SteamMatchmakingSessionController {
    private final SteamMatchmakingSessionService steamMatchmakingSessionService;

    public SteamMatchmakingSessionController(SteamMatchmakingSessionService steamMatchmakingSessionService) {
        this.steamMatchmakingSessionService = steamMatchmakingSessionService;
    }

    @PostMapping("/create")
    public Mono<SteamMatchmakingSessionDto> create(@RequestBody SteamMatchmakingSessionDto dto) {
        return steamMatchmakingSessionService.create(dto);
    }

    @PutMapping("/update/{sessionId}")
    public Mono<SteamMatchmakingSessionDto> update(
            @PathVariable("sessionId") String sessionId,
            @RequestBody SteamMatchmakingSessionDto dto) {
        return steamMatchmakingSessionService.update(sessionId, dto);
    }

    @DeleteMapping("/delete/{sessionId}")
    public Mono<Void> delete(@PathVariable("sessionId") String sessionId) {
        return steamMatchmakingSessionService.delete(sessionId);
    }

    @GetMapping("/list")
    public Flux<SteamMatchmakingSessionDto> listAll() {
        return steamMatchmakingSessionService.listAllSessions();
    }
}