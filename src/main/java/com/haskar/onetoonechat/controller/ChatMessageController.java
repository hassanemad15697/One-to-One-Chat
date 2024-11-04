package com.haskar.onetoonechat.controller;


import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.model.ChatNotification;
import com.haskar.onetoonechat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @MessageMapping("/chat")
    public void saveMessage(@Payload ChatMessage chatMessage){
        ChatMessage message = chatMessageService.save(chatMessage);
        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages",
                ChatNotification.builder()
                        .id(message.getId())
                        .senderId(message.getSenderId())
                        .recipientId(message.getRecipientId())
                        .content(message.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable String senderId,
                                                              @PathVariable String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
}
