package org.example.steammatchmakingservice.controller;

import org.example.steammatchmakingservice.dto.MatchmakingRequest;
import org.example.steammatchmakingservice.dto.MatchmakingResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MatchmakingSocketController {

    @MessageMapping("/matchmaking")
    @SendTo("topic/matchmaking-updates")
    public MatchmakingResponse handleMatchmaking(MatchmakingRequest request) {
        // Process matchmaking request
        System.out.println("Receieved matchmaking request: " + request);
        return new MatchmakingResponse("Match found for: " + request.userId());
    }
}
