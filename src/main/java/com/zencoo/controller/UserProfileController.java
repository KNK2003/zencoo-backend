package com.zencoo.controller;

import com.zencoo.dto.UserProfileDto;
import com.zencoo.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        return profileOpt
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
    }
}