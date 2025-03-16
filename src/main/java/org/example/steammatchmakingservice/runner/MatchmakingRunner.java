package org.example.steammatchmakingservice.runner;

import org.example.steammatchmakingservice.redis.ReactiveRedisSubscriber;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingRunner implements CommandLineRunner {

    private final ReactiveRedisSubscriber reactiveRedisSubscriber;

    public MatchmakingRunner(ReactiveRedisSubscriber reactiveRedisSubscriber) {
        this.reactiveRedisSubscriber = reactiveRedisSubscriber;
    }

    @Override
    public void run(String... args) {
        reactiveRedisSubscriber.startListeningInviteMsg();
        reactiveRedisSubscriber.startListeningAcceptInviteMsg();
    }
}
