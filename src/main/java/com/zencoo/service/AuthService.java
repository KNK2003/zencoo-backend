package com.zencoo.service;

import com.zencoo.model.User;
import com.zencoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    public boolean validateLogin(String email, String password) {
        logger.info("Validating login for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for email: {}", email);
            return false;
        }
        String dbPassword = userOpt.get().getPasswordHash();
        logger.info("DB password hash for email {}: {}", email, dbPassword);
        logger.info("Input password for email {}: {}", email, password);
        
        boolean matches;
        try {
            matches = passwordEncoder.matches(password, dbPassword);
            logger.info("Password matches for email {}: {}", email, matches);
        } catch (Exception e) {
            logger.error("Error during password matching for email {}: {}", email, e.getMessage());
            // Fallback to plain text comparison for legacy passwords
            matches = password.equals(dbPassword);
            logger.info("Fallback plain text comparison for email {}: {}", email, matches);
        }
        
        return matches;
    }

    public boolean isEmailRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUsernameUnique(String username) {
        return !userRepository.existsByUsername(username);
    }

    public User registerUser(String email, String username, String password, String fullName, String doorNumber, String community) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setDoorNumber(doorNumber);
        user.setCommunity(community);
        return userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}