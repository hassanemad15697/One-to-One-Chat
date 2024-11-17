package com.haskar.onetoonechat.model;

import com.haskar.onetoonechat.model.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;
    private String chatSessionId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
    private MessageType messageType;
}
