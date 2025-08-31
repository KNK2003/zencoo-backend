package com.zencoo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime timestamp;

    public enum Status {
        PENDING, ACCEPTED, DECLINED
    }
}