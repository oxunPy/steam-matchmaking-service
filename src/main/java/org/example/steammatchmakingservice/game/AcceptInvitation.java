package org.example.steammatchmakingservice.game;

public record AcceptInvitation(
   String acceptorUsername,
   String senderUsername,
   boolean accepted
) {}
