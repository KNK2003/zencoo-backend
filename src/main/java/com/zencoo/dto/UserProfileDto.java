package com.zencoo.dto;

import java.sql.Timestamp;

public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String doorNumber;
    private String bio;
    private String hometown; 
    private String profilePic;
    private Timestamp lastUsernameChange; // <-- Add this line
    private String headerBg;

    // Constructors
    public UserProfileDto() {}
    public UserProfileDto(Long id, String username, String email, String fullName, String doorNumber,
                      String bio, String hometown, String profilePic, Timestamp lastUsernameChange, String headerBg) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.doorNumber = doorNumber;
        this.bio = bio;
        this.hometown = hometown;
        this.profilePic = profilePic;
        this.lastUsernameChange = lastUsernameChange;
        this.headerBg = headerBg;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDoorNumber() { return doorNumber; }
    public void setDoorNumber(String doorNumber) { this.doorNumber = doorNumber; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getHometown() { return hometown; }
    public void setHometown(String hometown) { this.hometown = hometown; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public Timestamp getLastUsernameChange() { return lastUsernameChange; }
    public void setLastUsernameChange(Timestamp lastUsernameChange) { this.lastUsernameChange = lastUsernameChange; }

    public String getHeaderBg() { return headerBg; }
    public void setHeaderBg(String headerBg) { this.headerBg = headerBg; }
}