package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.exception.ResourceNotFoundException;
import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.model.LastMessage;
import com.haskar.onetoonechat.respository.ChatMessageRepository;
import com.haskar.onetoonechat.respository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageService {


    private final ChatMessageRepository chatMessageRepository;

    private final ChatSessionRepository chatSessionRepository;

    public ChatMessage createChatMessage(ChatMessage chatMessage) {
        // Ensure chat session exists
        ChatSession chatSession = chatSessionRepository.findById(chatMessage.getChatSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("ChatSession not found"));
        chatMessage.setTimestamp(new Date());
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Update lastMessage in ChatSession
        LastMessage lastMessage = LastMessage.builder()
                .content(chatMessage.getContent())
                .senderId(chatMessage.getSenderId())
                .timestamp(chatMessage.getTimestamp())
                .build();
        chatSession.setLastMessage(lastMessage);
        chatSession.setUpdatedAt(new Date());
        chatSessionRepository.save(chatSession);

        return savedMessage;
    }
    public Page<ChatMessage> getChatMessages(String chatId, Pageable pageable) {
        return chatMessageRepository.findByChatSessionId(chatId, pageable);
    }
}
