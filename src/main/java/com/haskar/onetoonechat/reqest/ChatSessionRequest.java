package com.haskar.onetoonechat.reqest;


import com.haskar.onetoonechat.model.enums.ChatType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionRequest {

    @NotBlank(message = "User ID A must not be blank.")
    private String userIdA;

    @NotBlank(message = "User ID B must not be blank.")
    private String userIdB;

    @NotBlank(message = "Chat type must be provided.")
    private ChatType chatType;

    private String initialMessageContent;

    private String initialMessageSenderId;
}
