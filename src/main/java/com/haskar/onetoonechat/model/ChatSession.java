package com.haskar.onetoonechat.model;

import com.haskar.onetoonechat.model.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_sessions")
@CompoundIndex(name = "participantsIds_idx", def = "{'participantsIds': 1, 'updatedAt': -1}")
public class ChatSession {

    @Id
    private String id;
    private Set<String> participantsIds;
    private ChatType chatType;
    private LastMessage lastMessage;
    private Date createdAt;
    private Date updatedAt;
}
