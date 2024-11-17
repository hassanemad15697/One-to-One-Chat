package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.Friendship;
import com.haskar.onetoonechat.model.enums.FriendshipStatus;
import com.haskar.onetoonechat.respository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public Friendship createFriendship(Friendship friendship) {
        // Ensure no duplicate friendship
        boolean exists = friendshipRepository.findAll().stream()
                .anyMatch(f -> (f.getUserId1().equals(friendship.getUserId1()) && f.getUserId2().equals(friendship.getUserId2()))
                        || (f.getUserId1().equals(friendship.getUserId2()) && f.getUserId2().equals(friendship.getUserId1())));
        if (exists) {
            throw new DuplicateKeyException("Friendship already exists");
        }
//        friendship.setLastInteractedAt(new Date());
        return friendshipRepository.save(friendship);
    }

    public Page<Friendship> getFriendships(String userId, Pageable pageable) {
        return friendshipRepository.findByUserId1OrUserId2(userId, userId, pageable);
    }

    public void updateFriendshipStatus(String friendshipId, FriendshipStatus status) throws ClassNotFoundException {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship not found"));
        friendship.setFriendshipStatus(status);
//        friendship.setLastInteractedAt(new Date());
        friendshipRepository.save(friendship);
    }



}
