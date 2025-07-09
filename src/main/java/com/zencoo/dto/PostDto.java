package com.zencoo.dto;

import com.zencoo.model.Post;

public class PostDto {
    public Long id;
    public String imageUrl;
    public String caption;
    public String createdAt;
    public Long userId;
    public String username;
    public String profilePic;
    public String displayName; 

    public PostDto(Post post) {
        this.id = post.getId();
        this.imageUrl = post.getImageUrl();
        this.caption = post.getCaption();
        this.createdAt = post.getCreatedAt() != null ? post.getCreatedAt().toString() : null;
        this.userId = post.getUser().getId();
        // Remove leading '@' if present
        String rawUsername = post.getUser().getUsername();
        this.username = (rawUsername != null && rawUsername.startsWith("@"))
            ? rawUsername.substring(1)
            : rawUsername;
        this.profilePic = post.getUser().getProfilePic();
        this.displayName = post.getUser().getFullName(); 
    }
}