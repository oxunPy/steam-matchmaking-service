package org.example.steammatchmakingservice.repository;

import org.example.steammatchmakingservice.entity.SteamMatchmakingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchmakingRepository extends JpaRepository<SteamMatchmakingRequest, Long>  {
}
