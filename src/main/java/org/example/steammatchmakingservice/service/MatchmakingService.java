package org.example.steammatchmakingservice.service;

import org.example.steammatchmakingservice.dto.MatchmakingRequestDto;
import org.example.steammatchmakingservice.dto.SteamMatchmakingRequestDto;
import org.example.steammatchmakingservice.dto.StoredMatchRequestDto;
import org.example.steammatchmakingservice.entity.SteamBaseEntity;
import org.example.steammatchmakingservice.entity.SteamMatchmakingRequest;
import org.example.steammatchmakingservice.entity.SteamMatchmakingSession;
import org.example.steammatchmakingservice.game.GameMode;
import org.example.steammatchmakingservice.repository.MatchmakingRedisRepository;
import org.example.steammatchmakingservice.response.MatchmakingResponse;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchmakingService {
    private final ReactiveRedisTemplate<String, StoredMatchRequestDto> redisSessionRequestsTemplate;            // sessionId - List<Requests>
    private final ReactiveRedisTemplate<String, String> redisRequestSessionTemplate;                    // requestId - sessionId
    private final MatchmakingRedisRepository redisRepository;
    private final Duration FINDING_MATCH_TIMEOUT = Duration.ofMinutes(20);
    private final Duration EXPIRING_PAIR_TIMEOUT = Duration.ofMinutes(40);

    private final SteamMatchmakingRequestService steamMatchmakingRequestService;
    private final SteamMatchmakingSessionService steamMatchmakingSessionService;

    public MatchmakingService(ReactiveRedisTemplate<String, StoredMatchRequestDto> redisSessionRequestsTemplate, ReactiveRedisTemplate<String, String> requestSessionTemplate,
                              ReactiveRedisTemplate<String, String> redisRequestSessionTemplate, MatchmakingRedisRepository redisRepository,
                              SteamMatchmakingRequestService steamMatchmakingRequestService, SteamMatchmakingSessionService steamMatchmakingSessionService) {
        this.redisSessionRequestsTemplate = redisSessionRequestsTemplate;
        this.redisRequestSessionTemplate = redisRequestSessionTemplate;
        this.redisRepository = redisRepository;
        this.steamMatchmakingRequestService = steamMatchmakingRequestService;
        this.steamMatchmakingSessionService = steamMatchmakingSessionService;
    }

    public Mono<Void> processMatchmaking(MatchmakingRequestDto request) {
        if(request == null || request.requestId() == null)
            return Mono.empty();

        return steamMatchmakingSessionService.findFirstAvailableMatchSessionOrCreate(request)
                .flatMap(validSession -> {
                    SteamMatchmakingSession updatedSession = SteamMatchmakingSession.copyFrom(validSession);
                    List<String> updatedPlayers = new ArrayList<>(validSession.getPlayers());
                    updatedPlayers.add(request.mainPlayer());
                    updatedPlayers.addAll(request.friends());
                    updatedSession.setPlayers(updatedPlayers);
                    updatedSession.setOpenSlots(validSession.getOpenSlots() - 1 - request.friends().size());

                    return steamMatchmakingSessionService.update(updatedSession.getId().toString(), updatedSession.toDto())
                            .then(createNewMatchmakingRequest(updatedSession.getId().toString(), request))
                            .then(createNewRequestSessionPairInRedis(request.requestId(), updatedSession.getId().toString()))
                            .then(updateMatchSessionInRedis(updatedSession.getId().toString(), request))
                            .then(notifyMatchmakingUpdate(request.requestId()));
                })
//                .switchIfEmpty(createNewMatchmakingSession(request).then())
                .then();
    }

    public Mono<MatchmakingResponse> handleSteamMatch(MatchmakingRequestDto request) {
        return
        findMatchOpenSlotsByRequestId(request.gameMode(), request.requestId())
                .flatMap(openSlots -> {
                    if(openSlots <= 0) {
                        return removeSessionAndRequestsFromRedis(request.requestId())
                                .then(closeMatchSessionDB(request.requestId()))
                                .thenReturn(MatchmakingResponse.builder()
                                        .status(MatchmakingResponse.Status.SUCCESS).build());
                    } else {
                        return notifyMatchmakingUpdate(request.requestId())
                                .then(Mono.just(MatchmakingResponse.builder()
                                        .message("Waiting players to join...")
                                        .status(MatchmakingResponse.Status.WAIT_TIME)
                                        .build()));
                    }
                })
                .switchIfEmpty(Mono.just(MatchmakingResponse.builder().status(MatchmakingResponse.Status.FAILED).message("Timeout error!").build()));
    }


    private Mono<SteamMatchmakingRequestDto> createNewMatchmakingRequest(String sessionId, MatchmakingRequestDto request) {
        SteamMatchmakingRequest newRequest = new SteamMatchmakingRequest();
        newRequest.getPlayers().add(request.mainPlayer());
        newRequest.getPlayers().addAll(request.friends());
        newRequest.setSessionId(sessionId);

        return steamMatchmakingRequestService.createRequest(newRequest.toDto());
    }

    public Mono<MatchmakingResponse> createNewMatchmakingSession(MatchmakingRequestDto request) {
        SteamMatchmakingSession newSession = SteamMatchmakingSession.initNew(request.gameMode());
        newSession.getPlayers().add(request.mainPlayer());
        newSession.getPlayers().addAll(request.friends());
        newSession.setOpenSlots(request.gameMode().maxPlayers() - newSession.getPlayers().size());
        newSession.setPlayers(newSession.getPlayers());
        return steamMatchmakingSessionService.create(newSession.toDto())
                .flatMap(session -> {
                    Mono<SteamMatchmakingRequestDto> firstReq = createNewMatchmakingRequest(session.getId().toString(), request);

                    return firstReq.flatMap(newRequest -> {
                        StoredMatchRequestDto storedData = new StoredMatchRequestDto();
                        storedData.getPlayers().addAll(newRequest.getPlayers());
                        storedData.setSessionId(session.getId().toString());
                        storedData.setId(newRequest.getId().toString());
                        return createNewRequestSessionPairInRedis(newRequest.getId().toString(), session.getId().toString())
                                .then(createNewSessionRequestsPairListInRedis(session.getId().toString(), storedData))
                                .flatMap(result -> {
                                    return Mono.just(MatchmakingResponse.builder().sessionId(newSession.getId().toString()).status(MatchmakingResponse.Status.WAIT_TIME).build());
                                });
                    });

                })
                .doOnError(error -> System.out.println("Error saving " + error.getMessage()));
    }

    public Mono<Void> notifyMatchmakingUpdate(String requestId) {
        // here will be the code for the real time updating using web sockets
        return Mono.empty();
    }

    private Mono<Void> updateMatchInDB(SteamMatchmakingRequest match) {
        return steamMatchmakingRequestService.updateRequest(match.getId().toString(), match.toDto()).then();
    }

    private Mono<Void> removeMatchFromRedis(StoredMatchRequestDto data) {
        return redisSessionRequestsTemplate.opsForValue().delete(data.getId()).then();
    }

    private Mono<Void> closeMatchSessionDB(String requestId) {
        Mono<SteamMatchmakingSession> steamSession = steamMatchmakingSessionService.findMatchSessionByJoinRequestId(requestId);
        return
                steamSession.flatMap(session -> {
                    session.setClosed(true);
                    session.setOpenSlots(0);
                    session.setStatus(SteamBaseEntity.SessionStatus.PLAYING_GAME);
                    return steamMatchmakingSessionService.update(session.getId().toString(), session.toDto());
                }).then();
    }

    private Mono<Void> createNewRequestSessionPairInRedis(String requestId, String sessionId) {
        return redisRequestSessionTemplate.opsForValue().set(requestId, sessionId)
                .doOnSuccess(result -> System.out.println("Caching request-session pair in redis " + result))
                .doOnError(error -> System.out.println("Error caching : " + error.getMessage()))
                .then(redisRequestSessionTemplate.expire(requestId, EXPIRING_PAIR_TIMEOUT))
                .then();
    }

    private Mono<Void> createNewSessionRequestsPairListInRedis(String sessionId, StoredMatchRequestDto storeData) {
        return redisSessionRequestsTemplate.opsForList()
                .rightPush(sessionId, storeData)
                .doOnSuccess(result -> System.out.println("Caching session-storedData in redis " + result))
                .doOnError(error -> System.out.println("Error caching : " + error.getMessage()))
                .then(redisRequestSessionTemplate.expire(sessionId, FINDING_MATCH_TIMEOUT))
                .then();
    }

    private Mono<Void> updateMatchSessionInRedis(String sessionId, MatchmakingRequestDto request) {
        StoredMatchRequestDto storeData = new StoredMatchRequestDto();
        storeData.getPlayers().addAll(List.of(request.mainPlayer()));
        storeData.getPlayers().addAll(request.friends());
        storeData.setId(request.requestId());

        return createNewSessionRequestsPairListInRedis(sessionId, storeData).then();
    }


    public Mono<Integer> findMatchOpenSlotsByRequestId(GameMode mode, String requestId) {
        // find sessionId first
        return redisRequestSessionTemplate.opsForValue().get(requestId)
                .flatMap(sessionId -> {
                    return
                    redisSessionRequestsTemplate.opsForList().size(sessionId).
                            flatMap(size -> {
                                return
                                redisSessionRequestsTemplate.opsForList().range(sessionId, 0, size)
                                        .collectList()
                                        .flatMap(requests -> {
                                            int openSlots = mode.maxPlayers();
                                            for(StoredMatchRequestDto req : requests) {
                                                openSlots -= req.getPlayers().size();
                                            }

                                            return Mono.just(openSlots);
                                        });
                            });
                });
    }

    private Mono<Long> removeSessionAndRequestsFromRedis(String requestId) {
        return redisRequestSessionTemplate.opsForValue().get(requestId)
                .flatMap(sessionId -> {
                    return redisSessionRequestsTemplate.opsForList().size(sessionId)
                            .flatMap(size -> {
                                return
                                redisSessionRequestsTemplate.opsForList().range(sessionId, 0, size)
                                        .collectList()
                                        .flatMap(requests -> {
                                            List<String> reqKeys = new ArrayList<>();
                                            for(StoredMatchRequestDto req : requests) {
                                                reqKeys.add(req.getId());
                                            }
                                            return redisRequestSessionTemplate.delete(Flux.fromArray(reqKeys.toArray(new String[0])));
                                        }).then(redisSessionRequestsTemplate.delete(sessionId));
                            });
                });
    }
}
