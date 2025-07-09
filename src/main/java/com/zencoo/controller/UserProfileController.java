package com.zencoo.controller;

import com.zencoo.dto.UserProfileDto;
import com.zencoo.model.User; // <-- Add this line
import com.zencoo.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Adjust origins as needed for your mobile frontend
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal(expression = "id") Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Optional<UserProfileDto> profileOpt = userProfileService.getUserProfileById(userId);
        // If your service does not yet return lastUsernameChange, update it!
        return profileOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        Optional<UserProfileDto> profileOpt = userProfileService.getUserProfileById(id);
        return profileOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    @PatchMapping("/profile/bio")
    public ResponseEntity<?> updateBio(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody Map<String, String> body
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String bio = body.get("bio");
        if (bio == null) {
            return ResponseEntity.badRequest().body("Bio is required");
        }
        Optional<User> userOpt = userProfileService.updateUserBio(userId, bio);
        return userOpt
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getDoorNumber(),
                    user.getBio(),
                    user.getHometown(),
                    user.getProfilePic(),
                    user.getLastUsernameChange(),
                    user.getHeaderBg() // <-- Add this argument
                )))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    @PatchMapping("/profile/hometown")
    public ResponseEntity<?> updateHometown(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody Map<String, String> body
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String hometown = body.get("hometown");
        if (hometown == null) {
            return ResponseEntity.badRequest().body("Hometown is required");
        }
        Optional<User> userOpt = userProfileService.updateUserHometown(userId, hometown);
        return userOpt
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getDoorNumber(),
                    user.getBio(),
                    user.getHometown(),
                    user.getProfilePic(),
                    user.getLastUsernameChange(),
                    user.getHeaderBg() // <-- Add this argument
                )))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    @PatchMapping("/profile/profile-pic")
    public ResponseEntity<?> updateProfilePic(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody Map<String, String> body
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String profilePic = body.get("profilePic");
        if (profilePic == null) {
            return ResponseEntity.badRequest().body("profilePic is required");
        }
        Optional<User> userOpt = userProfileService.updateUserProfilePic(userId, profilePic);
        return userOpt
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getDoorNumber(),
                    user.getBio(),
                    user.getHometown(),
                    user.getProfilePic(),
                    user.getLastUsernameChange(),
                    user.getHeaderBg() // <-- Add this argument
                )))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody Map<String, String> body
    ) {
        String displayName = body.get("displayName");
        String username = body.get("username");
        String door = body.get("door");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        try {
            Optional<User> userOpt = userProfileService.updateUserProfile(userId, displayName, username, door);
            return userOpt
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(new UserProfileDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getDoorNumber(),
                    user.getBio(),
                    user.getHometown(),
                    user.getProfilePic(),
                    user.getLastUsernameChange(),
                    user.getHeaderBg() // <-- Add this argument
                )))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @PatchMapping("/profile/header-bg")
    public ResponseEntity<?> updateHeaderBg(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String headerBg = body.get("headerBg");
        if (headerBg == null) {
            return ResponseEntity.badRequest().body("headerBg is required");
        }
        Optional<User> userOpt = userProfileService.updateUserHeaderBg(userId, headerBg);
        return userOpt
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of("headerBg", user.getHeaderBg())))
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }
}