package org.example.steammatchmakingservice.game;

public record NoteData (
    String username,
    String sessionId,
    String payload
) {}
