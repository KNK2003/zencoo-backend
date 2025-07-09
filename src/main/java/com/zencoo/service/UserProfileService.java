package com.zencoo.service;

import com.zencoo.dto.UserProfileDto;
import com.zencoo.model.User;
import com.zencoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public Optional<UserProfileDto> getUserProfileById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> new UserProfileDto(
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
        ));
    }

    public Optional<User> updateUserBio(Long userId, String bio) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBio(bio);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> updateUserHometown(Long userId, String hometown) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setHometown(hometown);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> updateUserProfilePic(Long userId, String profilePic) {
        Optional<User> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(user -> {
            String oldPic = user.getProfilePic();
            // Only delete if removing and oldPic is a Cloudinary URL
            if (profilePic.isEmpty() && oldPic != null && !oldPic.isEmpty() && oldPic.contains("res.cloudinary.com")) {
                cloudinaryService.deleteImageByUrl(oldPic);
            }
            user.setProfilePic(profilePic.isEmpty() ? "" : profilePic);
            userRepository.save(user);
        });
        return userOpt;
    }

    public Optional<User> updateUserProfile(Long userId, String displayName, String username, String doorNumber) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return Optional.empty();
        User user = userOpt.get();

        // Validate displayName
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Display name cannot be empty.");
        }
        // Validate doorNumber
        if (!doorNumber.matches("\\d{4}")) {
            throw new ResponseStatusException(BAD_REQUEST, "Door number must be 4 digits.");
        }

        // Username change logic
        if (!username.equals(user.getUsername())) {
            if (user.getLastUsernameChange() != null &&
                Duration.between(user.getLastUsernameChange().toInstant(), Instant.now()).toDays() < 14) {
                throw new ResponseStatusException(BAD_REQUEST, "You can only change your username once every 14 days.");
            }
            if (userRepository.existsByUsername(username)) {
                throw new ResponseStatusException(CONFLICT, "Username already taken.");
            }
            user.setUsername(username);
            user.setLastUsernameChange(Timestamp.from(Instant.now()));
        }

        user.setFullName(displayName);
        user.setDoorNumber(doorNumber);
        userRepository.save(user);
        return Optional.of(user);
    }

    public Optional<User> updateUserHeaderBg(Long userId, String headerBg) {
        Optional<User> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(user -> {
            user.setHeaderBg(headerBg);
            userRepository.save(user);
        });
        return userOpt;
    }
}