package com.haskar.onetoonechat.model;

import com.haskar.onetoonechat.model.enums.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friendships")
public class Friendship {

    @Id
    private String id;

    private String userId1;
    private String userId2;
    private FriendshipStatus friendshipStatus;
}
