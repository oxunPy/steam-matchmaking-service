package org.example.steammatchmakingservice.dto;


import org.example.steammatchmakingservice.game.GameMode;

import java.util.List;

public record MatchmakingRequestDto (
   String requestId,

   String mainPlayer,

   GameMode gameMode,

   List<String> friends,

   int maxPlayers
) {}
