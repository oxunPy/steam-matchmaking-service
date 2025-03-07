package org.example.steammatchmakingservice.entity;

import org.example.steammatchmakingservice.dto.SteamMatchmakingSessionDto;
import org.example.steammatchmakingservice.game.GameMode;
import org.springframework.beans.BeanUtils;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;


@Table(name = "matchmaking_sessions")
public class SteamMatchmakingSession extends SteamBaseEntity {
    @Column("game_id")
    private String gameId;

    @Column("players")
    private List<String> players;

    @Column("total_players")
    private int totalPlayers;

    @Column("open_slots")
    private int openSlots;

    @Column("is_closed")
    private boolean isClosed = false;

    public static SteamMatchmakingSession initNew(GameMode mode) {
        SteamMatchmakingSession newSession = new SteamMatchmakingSession();
        newSession.setStatus(SessionStatus.OPEN);
        newSession.setPlayers(new ArrayList<>());
        newSession.setTotalPlayers(mode.maxPlayers());
        newSession.setClosed(false);
        return newSession;
    }

    public SteamMatchmakingSession() {
        players = new ArrayList<>();
    }

    public static SteamMatchmakingSession copyFrom(SteamMatchmakingSession t) {
        SteamMatchmakingSession newS = new SteamMatchmakingSession();
        BeanUtils.copyProperties(t, newS, "players");
        return newS;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getOpenSlots() {
        return openSlots;
    }

    public void setOpenSlots(int openSlots) {
        this.openSlots = openSlots;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public SteamMatchmakingSessionDto toDto() {
        SteamMatchmakingSessionDto dto = new SteamMatchmakingSessionDto();
        BeanUtils.copyProperties(this, dto, "players", "status");
        dto.setPlayers(getPlayers());
        return dto;
    }
}
