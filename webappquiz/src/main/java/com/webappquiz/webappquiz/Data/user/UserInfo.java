package com.webappquiz.webappquiz.data.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "userInfo")
public class UserInfo {
    @Id
    private String objectId;
    
    private String userId = "";
    private String password = "";
    private String userName = "";
    private String bCode = "";
    
}