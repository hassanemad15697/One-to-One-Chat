package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    Page<Friendship> findByUserId1OrUserId2(String userId1, String userId2, Pageable pageable);
}
