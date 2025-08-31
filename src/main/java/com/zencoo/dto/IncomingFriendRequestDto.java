package com.zencoo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomingFriendRequestDto {
    private Long id;
    private Long senderId;
    private String username;
    private String fullName;
    private String doorNumber;
    private String profilePic;
}