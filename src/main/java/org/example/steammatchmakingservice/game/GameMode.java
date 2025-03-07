package org.example.steammatchmakingservice.game;

public enum GameMode {
    DEATHMATCH(20),
    COMPETETIVE(10),
    CASUAL(10);

    GameMode(int mp) {
        maxPlayers = mp;
    }

    private final int maxPlayers;
    public int maxPlayers() {
        return maxPlayers;
    }
}
