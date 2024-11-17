package com.haskar.onetoonechat.controller;

import com.haskar.onetoonechat.model.Friendship;
import com.haskar.onetoonechat.model.enums.FriendshipStatus;
import com.haskar.onetoonechat.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    // Create Friendship
    @PostMapping
    public ResponseEntity<Friendship> createFriendship(@RequestBody Friendship friendship) {
        Friendship createdFriendship = friendshipService.createFriendship(friendship);
        return ResponseEntity.ok(createdFriendship);
    }

    // Get Friendships
    @GetMapping
    public ResponseEntity<Page<Friendship>> getFriendships(
            @RequestParam String userId,
            Pageable pageable) {
        Page<Friendship> friendships = friendshipService.getFriendships(userId, pageable);
        return ResponseEntity.ok(friendships);
    }

//    // Update Friendship Status
//    @PatchMapping("/{id}")
//    public ResponseEntity<Void> updateFriendshipStatus(
//            @PathVariable String id,
//            @RequestParam FriendshipStatus status) {
//        friendshipService.updateFriendshipStatus(id, status);
//        return ResponseEntity.noContent().build();
//    }
//
//    // Delete Friendship
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFriendship(@PathVariable String id) {
//        friendshipService.deleteFriendship(id);
//        return ResponseEntity.noContent().build();
//    }
}

