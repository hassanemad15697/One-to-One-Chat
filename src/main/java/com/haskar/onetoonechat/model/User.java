package com.haskar.onetoonechat.model;

import com.haskar.onetoonechat.model.enums.Gender;
import com.haskar.onetoonechat.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String fullName;
    private String nickName;
    private String email;
    private Gender gender;
    private Status status;
    private Settings settings;
    private Date createdAt;
    private Date updatedAt;
}
