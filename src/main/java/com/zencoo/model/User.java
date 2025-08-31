package com.zencoo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "door_number")
    private String doorNumber;

    @Column(name = "community")
    private String community;

    @Column(name = "bio")
    private String bio;

    @Column(name = "hometown")
    private String hometown;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "profile_pic")
    private String profilePic;

    @Column(name = "last_username_change")
    private Timestamp lastUsernameChange;

    @Column(name = "header_bg")
    private String headerBg;

    @Column(name = "friends_count")
    private Integer friendsCount;

    // Custom constructor for selected fields
    public User(String email, String username, String passwordHash, String fullName, String doorNumber, String community) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.doorNumber = doorNumber;
        this.community = community;
    }
}