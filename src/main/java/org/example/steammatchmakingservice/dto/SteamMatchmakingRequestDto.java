package org.example.steammatchmakingservice.dto;

import java.util.List;
import java.util.UUID;

public class SteamMatchmakingRequestDto {
    private UUID id;

    private List<String> players;

    private String sessionId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
