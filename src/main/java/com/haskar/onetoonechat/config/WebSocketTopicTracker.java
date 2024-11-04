package com.haskar.onetoonechat.config;


import com.haskar.onetoonechat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WebSocketTopicTracker {

    private final UserService userService;

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
    public void handleUnsubscribeEvent(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String user = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");

        if (user != null) {
            userService.disconnect(user);
        }
    }

}
