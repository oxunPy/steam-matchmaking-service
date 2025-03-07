package org.example.steammatchmakingservice.entity;

import org.example.steammatchmakingservice.dto.SteamMatchmakingRequestDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "matchmaking_requests")
public class SteamMatchmakingRequest extends SteamBaseEntity {
    @Column("players")
    private List<String> players;

    @Column("session_id")
    private String sessionId;

    public SteamMatchmakingRequest(UUID id, LocalDateTime createdAt, SessionStatus status, List<String> players, String sessionId) {
        super(id, createdAt, status);
        this.players = players;
        this.sessionId = sessionId;
    }
    public SteamMatchmakingRequest() {
        players = new ArrayList<>();
    }

    public SteamMatchmakingRequest(List<String> players, String sessionId) {
        this.players = players;
        this.sessionId = sessionId;
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

    public SteamMatchmakingRequestDto toDto() {
        SteamMatchmakingRequestDto dto = new SteamMatchmakingRequestDto();
        BeanUtils.copyProperties(this, dto);
        dto.setSessionId(getSessionId());
        return dto;
    }
}
