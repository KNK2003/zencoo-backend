package com.zencoo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestDto {
    private Long id;
    private Long senderId;
    private Long receiverId; 
    private String displayName;
    private String username;
    private String door;
    private String wing;
    private String profilePic;
    private String timestamp;
}