package com.zencoo.controller;

import com.zencoo.model.User;
import com.zencoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/residents")
public class ResidentsController {

    @Autowired
    private UserRepository userRepository;

    // Add this above your class or method if using Spring Security
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Map<String, Object>> getResidents() {
        List<User> users = userRepository.findAll();

        System.out.println("Residents endpoint called, users found: " + users.size());
        return users.stream()
            .map(user -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", String.valueOf(user.getId()));
                map.put("displayName", user.getFullName() != null ? user.getFullName() : "");
                map.put("username", user.getUsername() != null ? user.getUsername() : "");
                map.put("door", user.getDoorNumber() != null ? user.getDoorNumber() : "");
                map.put("profilePic", user.getProfilePic()); 
                return map;
            })
            .collect(Collectors.toList());
    }
}