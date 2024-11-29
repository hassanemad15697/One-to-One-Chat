package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.model.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage , String> {
    Page<ChatMessage> findByChatSessionIdAndMessageTypeOrderByTimestampDesc(String chatId, MessageType message, Pageable pageable);

}
