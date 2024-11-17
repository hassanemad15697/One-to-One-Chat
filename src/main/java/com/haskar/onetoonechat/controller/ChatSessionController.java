package com.haskar.onetoonechat.controller;

import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-session")
public class ChatSessionController {

    private final ChatSessionService chatSessionService;

    // Create ChatSession
    @PostMapping
    public ResponseEntity<ChatSession> getChatSessionOrCreateIfMissing(@RequestBody ChatSession chatSession) {
        ChatSession createdChatSession = chatSessionService.getChatSessionOrCreateIfMissing(chatSession);
        return ResponseEntity.ok(createdChatSession);
    }

    // Get ChatSessions for User
    @GetMapping("/all/{userId}")
    public ResponseEntity<Page<ChatSession>> getUserChatSessions(
            @PathVariable("userId") String userId,
            Pageable pageable) {
        Page<ChatSession> chatSessions = chatSessionService.getUserChatSessions(userId, pageable);
        return ResponseEntity.ok(chatSessions);
    }

//    // Update ChatSession
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateChatSession(@PathVariable String id, @RequestBody ChatSession chatSession) {
//        chatSessionService.updateChatSession(id, chatSession);
//        return ResponseEntity.noContent().build();
//    }
//
//    // Delete ChatSession
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteChatSession(@PathVariable String id) {
//        chatSessionService.deleteChatSession(id);
//        return ResponseEntity.noContent().build();
//    }
}
