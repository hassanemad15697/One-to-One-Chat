package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.model.ChatMessage;
import com.haskar.onetoonechat.model.ChatSession;
import com.haskar.onetoonechat.model.User;
import com.haskar.onetoonechat.model.enums.MessageType;
import com.haskar.onetoonechat.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static com.haskar.onetoonechat.model.enums.Status.OFFLINE;
import static com.haskar.onetoonechat.model.enums.Status.ONLINE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectService {
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatSessionService chatSessionService;
    private final UserService userService;
    private final ChatMessageService chatMessageService;

    private final String OFFLINE_STATUS = "OFFLINE";
    private final String ONLINE_STATUS = "ONLINE";

    public void disconnect(String userId){
        Optional<User> foundUser = userRepository.findById(userId);
        foundUser.ifPresent(usr -> {
            usr.setStatus(OFFLINE);
            userRepository.save(usr);
            notifyOnlineFriends(userId, OFFLINE_STATUS);
        });
    }

    public void connect(User user, SimpMessageHeaderAccessor accessor) {

        Optional<User> foundUser = userRepository.findById(user.getId());
        foundUser.ifPresent(usr -> {
            usr.setStatus(ONLINE);
            userRepository.save(usr);
            Objects.requireNonNull(accessor.getSessionAttributes()).put("userId", usr.getId());
            notifyOnlineFriends(user.getId(), ONLINE_STATUS);
        });

    }

    private void notifyOnlineFriends(String user, String STATUS) {
        Page<ChatSession> userChatSessions = chatSessionService.getPrivateUserChatSessions(user, null);
        if(!userChatSessions.isEmpty()){
            userChatSessions.get().forEach(chatSession -> {
                chatSession.getParticipantsIds().forEach(participant -> {
                    boolean isOnline = ONLINE.equals(userService.getUserStatus(participant));
                    if (isOnline) {
                        ChatMessage message = chatMessageService.createChatMessage(
                                ChatMessage
                                        .builder()
                                        .messageType(MessageType.ONLINE_OFFLINE)
                                        .chatSessionId(chatSession.getId())
                                        .senderId(chatSession.getUserId())
                                        .recipientId(participant)
                                        .timestamp(new Date())
                                        .content(STATUS)
                                        .build());
                        simpMessagingTemplate.convertAndSendToUser(
                                participant, "/queue/messages",
                                message
                        );
                    }
                });

            });
        }
    }
}
