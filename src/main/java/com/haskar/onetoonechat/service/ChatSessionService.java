package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.model.enums.ChatType;
import com.haskar.onetoonechat.respository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public Page<ChatSession> getUserChatSessions(String userId, Pageable pageable) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
    }

    public Page<ChatSession> getPrivateUserChatSessions(String userId, Pageable pageable) {
        return chatSessionRepository.findByUserIdAndChatTypeOrderByUpdatedAtDesc(userId, ChatType.PERSONAL, pageable);
    }
    public Page<ChatSession> getGroupUserChatSessions(String userId, Pageable pageable) {
        return chatSessionRepository.findByUserIdAndChatTypeOrderByUpdatedAtDesc(userId, ChatType.GROUP, pageable);
    }


    @Transactional
    public ChatSession getChatSessionOrCreateIfMissing(ChatSession chatSession) {
        if (Objects.isNull(chatSession.getId())){
           return saveChatSession(chatSession);
        }
        return  chatSessionRepository.findById(chatSession.getId()).or(
                () -> Optional.ofNullable(saveChatSession(chatSession))
        ).orElse(null);
    }


    private ChatSession saveChatSession(ChatSession chatSession) {
        chatSession.setCreatedAt(new Date());
        chatSession.setUpdatedAt(new Date());
        if(Objects.nonNull(chatSession.getParticipantsIds()) && !chatSession.getParticipantsIds().isEmpty()){
            chatSession.getParticipantsIds().forEach(s -> {
                chatSession.setId(null);
                chatSession.setUserId(s);
                ChatSession saved = chatSessionRepository.save(chatSession);
                log.info("saved id: {}", saved.getId());
            });
        }
        return null;
    }

    public void updateChatSession(String chatSessionId, ChatSession chatSession) throws ClassNotFoundException {
        if (!chatSessionRepository.existsById(chatSessionId)) {
            throw new ResourceNotFoundException("ChatSession not found");
        }
        chatSession.setId(chatSessionId);
        chatSession.setUpdatedAt(new Date());
        chatSessionRepository.save(chatSession);
    }

}
