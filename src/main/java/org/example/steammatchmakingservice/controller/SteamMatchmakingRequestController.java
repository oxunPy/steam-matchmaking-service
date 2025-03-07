package org.example.steammatchmakingservice.controller;

import org.example.steammatchmakingservice.dto.SteamMatchmakingRequestDto;
import org.example.steammatchmakingservice.service.SteamMatchmakingRequestService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/requests")
public class SteamMatchmakingRequestController {

    private final SteamMatchmakingRequestService steamMatchmakingRequestService;

    public SteamMatchmakingRequestController(SteamMatchmakingRequestService steamMatchmakingRequestService) {
        this.steamMatchmakingRequestService = steamMatchmakingRequestService;
    }

    @PostMapping("/create")
    public Mono<SteamMatchmakingRequestDto> create(@RequestBody SteamMatchmakingRequestDto dto) {
        return steamMatchmakingRequestService.createRequest(dto);
    }

    @PutMapping("/update/{requestId}")
    public Mono<SteamMatchmakingRequestDto> update(
            @PathVariable("requestId") String requestId,
            @RequestBody SteamMatchmakingRequestDto dto) {

        return steamMatchmakingRequestService.updateRequest(requestId, dto);
    }

    @DeleteMapping("/delete/{requestId}")
    public Mono<Void> deleteMatchmakingRequest(@PathVariable("requestId") String requestId) {
        return steamMatchmakingRequestService.deleteRequest(requestId);
    }

    @GetMapping("/list")
    public Flux<SteamMatchmakingRequestDto> listAll() {
        return steamMatchmakingRequestService.listAllRequests();
    }
}
