package com.haskar.onetoonechat.controller;

import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.saveUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/by-username/{nickname}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search/{nickname}")
    public ResponseEntity<List<User>> getUsersByNickname(@PathVariable String nickname) {
        List<User> users = userService.getUsersByNickname(nickname);
        return ResponseEntity.ok(users);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
//        User updatedUser = userService.updateUser(id, user);
//        return ResponseEntity.ok(updatedUser);
//    }
}
