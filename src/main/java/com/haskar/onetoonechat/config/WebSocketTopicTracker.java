package com.haskar.onetoonechat.config;


import com.haskar.onetoonechat.service.ConnectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketTopicTracker {

    private final ConnectService connectService;

//    @EventListener
//    public void handleSubscribeEvent(SessionSubscribeEvent event) {
//        System.out.println(Arrays.toString(event.getMessage().getPayload()));
//        System.out.println(event.getMessage().getHeaders());
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String destination = headerAccessor.getDestination();
//
//        if (destination != null) {
//            System.out.println("Subscribed to topic: " + destination);
//        }
//    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            return;
        }
        Object userIdObj = sessionAttributes.get("userId");
        if (!(userIdObj instanceof String userId) || userId.isBlank()) {
            return;
        }

        connectService.disconnect(userId);
    }

}
