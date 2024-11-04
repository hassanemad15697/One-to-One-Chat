package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom , String> {

    public Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId , String recipientId);
}
