package org.example.steammatchmakingservice.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;

public class SteamBaseEntity implements Persistable<UUID> {
    @Id
    private UUID id;

    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column("status")
    private SessionStatus status = SessionStatus.OPEN;

    public enum SessionStatus {
        OPEN,
        START_GAME,
        PLAYING_GAME,
        END_GAME
    }

    public SteamBaseEntity(UUID id, LocalDateTime createdAt, SessionStatus status) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
    }

    public SteamBaseEntity() {}

    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return status == null || status == SessionStatus.OPEN ||
                id == null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
