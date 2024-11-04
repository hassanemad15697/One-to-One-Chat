package com.haskar.onetoonechat.service;

import com.haskar.onetoonechat.model.ChatRoom;
import com.haskar.onetoonechat.respository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(String senderId , String recipientId, Boolean createNewChatRoomIfNotExists){
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewChatRoomIfNotExists){
                        String chatId =createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    public String createChatId(String senderId , String recipientId){
        String chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoom senderRecipientChatRoom = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSenderChatRoom = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        chatRoomRepository.save(senderRecipientChatRoom);
        chatRoomRepository.save(recipientSenderChatRoom);

        return chatId;
    }
}
