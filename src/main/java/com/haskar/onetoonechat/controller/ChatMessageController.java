package com.haskar.onetoonechat.controller;
import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat")
    public void saveMessage(@Payload ChatMessage chatMessage){
        ChatMessage message = chatMessageService.createChatMessage(chatMessage);

        simpMessagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages",
                chatMessage
        );
    }

    @GetMapping("/message/{chatSessionId}")
    public ResponseEntity<Page<ChatMessage>> getChatMessages(
            @PathVariable("chatSessionId") String chatSessionId,
            Pageable pageable) {
        Page<ChatMessage> messages = chatMessageService.getChatMessages(chatSessionId, pageable);
        return ResponseEntity.ok(messages);
    }
}
