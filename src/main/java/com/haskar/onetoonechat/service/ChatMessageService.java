package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.respository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {


    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;


    public ChatMessage save(ChatMessage chatMessage){
        String chatRoomId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true)
                .orElseThrow();

        chatMessage.setChatId(chatRoomId);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId , String recipientId){
        String chatId = chatRoomService.getChatRoomId(
                        senderId,
                        recipientId,
                        false)
                .orElse(null);

        if(Objects.isNull(chatId)){
            return new ArrayList<>();
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findByChatId(chatId);
        if(Objects.isNull(chatMessages) || chatMessages.isEmpty()) {
            return new ArrayList<>();
        }
        return chatMessages;
    }
}
