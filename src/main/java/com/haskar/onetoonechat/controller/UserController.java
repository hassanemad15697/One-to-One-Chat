package com.haskar.onetoonechat.controller;

import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/public")
    public User addUser(@Payload User user, SimpMessageHeaderAccessor accessor) {
        Objects.requireNonNull(accessor.getSessionAttributes()).put("username", user.getNickName());
        return userService.saveUser(user);
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public void disconnect(@Payload User user) {
        userService.disconnect(user.getNickName());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
