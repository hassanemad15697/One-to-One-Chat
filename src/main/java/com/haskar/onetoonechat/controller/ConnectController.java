package com.haskar.onetoonechat.controller;

import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.service.ConnectService;
import com.haskar.onetoonechat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ConnectController {

    private final ConnectService connectService;
    @MessageMapping("/user.connectUser")
    public void addUser(@Payload User user, SimpMessageHeaderAccessor accessor) {
        connectService.connect(user, accessor);
    }

    @MessageMapping("/user.disconnectUser")
    public void disconnect(@Payload User user) {
        connectService.disconnect(user.getId());
    }

}
