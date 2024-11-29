package com.haskar.onetoonechat.response;


import com.haskar.onetoonechat.model.LastMessage;
import com.haskar.onetoonechat.model.enums.ChatType;
import com.haskar.onetoonechat.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSessionResponse {
    private String id;
    private Set<String> participantsIds;
    private ChatType chatType;
    private LastMessage lastMessage;
    private Date createdAt;
    private Date updatedAt;
    private Status partnerStatus;
}