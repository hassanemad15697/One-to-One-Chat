package com.haskar.onetoonechat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.model.enums.ChatType;
import com.haskar.onetoonechat.model.enums.Status;
import com.haskar.onetoonechat.response.ChatSessionResponse;
import com.haskar.onetoonechat.respository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public Page<ChatSessionResponse> getUserChatSessions(String userId, Pageable pageable) {
        Page<ChatSession> chatSessions = chatSessionRepository.findByParticipantsIdsContainingOrderByUpdatedAtDesc(userId, pageable);

        List<ChatSessionResponse> chatSessionResponses = chatSessions.getContent().stream()
                .map(chatSession -> mapToChatSessionResponse(chatSession, userId))
                .toList();

        return new PageImpl<>(chatSessionResponses, pageable, chatSessions.getTotalElements());
    }

    public List<ChatSessionResponse> getPrivateUserChatSessions(String userId) {
        List<ChatSession> chatSessions = chatSessionRepository.findByParticipantsIdsContainingAndChatTypeOrderByUpdatedAtDesc(userId, ChatType.PERSONAL);

        return chatSessions.stream()
                .map(chatSession -> mapToChatSessionResponse(chatSession, userId))
                .toList();
    }

    public List<ChatSessionResponse> getGroupUserChatSessions(String userId, Pageable pageable) {
        List<ChatSession> chatSessions = chatSessionRepository.findByParticipantsIdsContainingAndChatTypeOrderByUpdatedAtDesc(userId, ChatType.GROUP);

        return chatSessions.stream()
                .map(chatSession -> mapToChatSessionResponse(chatSession, userId))
                .toList();
    }

    @Transactional
    public ChatSessionResponse getChatSessionOrCreateIfMissing(ChatSession chatSession) {
        ChatSession result;

        if (Objects.isNull(chatSession.getId())) {
            result = saveChatSession(chatSession);
        } else {
            result = chatSessionRepository.findById(chatSession.getId()).orElse(null);
        }

        return mapToChatSessionResponse(result, null);
    }

    private ChatSession saveChatSession(ChatSession chatSession) {
        chatSession.setCreatedAt(new Date());
        chatSession.setUpdatedAt(new Date());

        if (Objects.nonNull(chatSession.getParticipantsIds()) && !chatSession.getParticipantsIds().isEmpty()) {
            chatSession.setId(null);
            return chatSessionRepository.save(chatSession);
        }

        return null;
    }

    private ChatSessionResponse mapToChatSessionResponse(ChatSession chatSession, String userId) {
        if (chatSession == null) {
            return null;
        }

        // Use ObjectMapper to map fields
        ChatSessionResponse chatSessionResponse = objectMapper.convertValue(chatSession, ChatSessionResponse.class);

        // Set participantsIds (ensures the field is mapped correctly)
        chatSessionResponse.setParticipantsIds(chatSession.getParticipantsIds());

        // Enhance with partner status if needed
        String partnerId = null;
        if (userId != null && chatSession.getParticipantsIds() != null) {
            partnerId = chatSession.getParticipantsIds().stream()
                    .filter(participant -> !participant.equals(userId))
                    .findFirst()
                    .orElse(null);
        }

        Status partnerStatus = partnerId != null ? userService.getUserStatus(partnerId) : null;
        chatSessionResponse.setPartnerStatus(partnerStatus);

        return chatSessionResponse;
    }
}
