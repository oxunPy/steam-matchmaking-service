package org.example.steammatchmakingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "matchmaking_requests")
public class SteamMatchmakingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerId;
    private int totalPlayers;
    private int openSlots;

    @ElementCollection
    @CollectionTable(name = "matchmaking_friends", joinColumns = @JoinColumn(name = "matchmaking_id"))
    @Column(name = "friend_id")
    private List<Long> friends;

    private LocalDateTime createdAt = LocalDateTime.now();
}
