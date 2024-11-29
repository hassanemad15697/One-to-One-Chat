package com.haskar.onetoonechat.respository;

import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.model.enums.ChatType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    Page<ChatSession> findByParticipantsIdsContainingOrderByUpdatedAtDesc(String userId,Pageable pageable);
    List<ChatSession> findByParticipantsIdsContainingAndChatTypeOrderByUpdatedAtDesc(String userId, ChatType chatType);
}
