package org.example.steammatchmakingservice.dto;

import java.util.ArrayList;
import java.util.List;

public class StoredMatchRequestDto {
    private String id;

    private String sessionId;

    private List<String> players = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
