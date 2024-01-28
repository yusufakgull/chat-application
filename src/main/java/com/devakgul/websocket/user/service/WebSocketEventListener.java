package com.devakgul.websocket.user.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = (String) headerAccessor.getSessionAttributes().get("username");
        if (Objects.nonNull(userName)) {
            log.info("User disconnected: {}", userName);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        if (Objects.isNull(headerAccessor.getSessionAttributes())) {
            log.info("Silent connect, no username available yet. Session id= " + headerAccessor.getSessionId());
        } else {
            String userName = (String) headerAccessor.getSessionAttributes().get("username");
            if (Objects.nonNull(userName)) {
                log.info("User connected: {}", userName);
            }
        }
    }
}
