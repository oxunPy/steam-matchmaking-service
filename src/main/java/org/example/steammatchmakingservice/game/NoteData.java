package org.example.steammatchmakingservice.game;

import java.util.List;

public record NoteData (
    String sender,
    List<String> receiverPlayers,
    String info
) {}
