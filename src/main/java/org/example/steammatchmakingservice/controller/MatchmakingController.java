package org.example.steammatchmakingservice.controller;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.example.steammatchmakingservice.service.kafka.MatchmakingProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/steam-matchmaking")
public class MatchmakingController {
    private final MatchmakingProducerService matchmakingProducer;

    public MatchmakingController(MatchmakingProducerService matchmakingProducer) {
        this.matchmakingProducer = matchmakingProducer;
    }

    @PostMapping("/request")
    public void requestMatchmaking(@RequestParam String userId,
                                   @RequestParam List<String> friends,
                                   int maxPlayers) {
        MatchmakingRequest request = new MatchmakingRequest(UUID.randomUUID().toString(), userId, friends, maxPlayers);
        matchmakingProducer.sendMatchmakingRequest(request);
    }
}
