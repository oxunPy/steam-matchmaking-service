package org.example.steammatchmakingservice.dto;


import java.util.List;

public record MatchmakingRequest(
   String requestId,
   String userId,
   List<String> friedIds,
   int maxPlayers
) {}
