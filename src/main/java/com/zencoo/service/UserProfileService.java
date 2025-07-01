package com.zencoo.service;

import com.zencoo.dto.UserProfileDto;
import com.zencoo.model.User;
import com.zencoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserProfileDto> getUserProfileById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> new UserProfileDto(
                user.getId(),
                user.getUsername(), // username
                user.getEmail(),    // email
                user.getFullName(),
                user.getDoorNumber()
        ));
    }
}