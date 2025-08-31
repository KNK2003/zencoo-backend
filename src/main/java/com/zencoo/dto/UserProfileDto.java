package com.zencoo.dto;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String doorNumber;
    private String bio;
    private String hometown; 
    private String profilePic;
    private Timestamp lastUsernameChange;
    private String headerBg;
}