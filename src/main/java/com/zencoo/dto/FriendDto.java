package com.zencoo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendDto {
    private Long userId;
    private String username;
    private String fullName;
    private String profilePic;
}