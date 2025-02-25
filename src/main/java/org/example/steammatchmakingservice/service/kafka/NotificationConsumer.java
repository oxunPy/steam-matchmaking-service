package org.example.steammatchmakingservice.service.kafka;


import org.example.steammatchmakingservice.service.WebSocketService;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    private final WebSocketService webSocketService;

    public NotificationConsumer(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }
}
