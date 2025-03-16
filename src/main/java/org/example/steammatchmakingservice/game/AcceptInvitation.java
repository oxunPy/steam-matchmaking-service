package org.example.steammatchmakingservice.game;

public record AcceptInvitation(
   String senderUsername,
   boolean accepted
) {}
