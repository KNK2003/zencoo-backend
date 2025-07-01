package com.zencoo.controller;

// import com.zencoo.util.GoogleTokenVerifier;
// import com.zencoo.util.JwtUtil;
// import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.zencoo.service.AuthService;
import com.zencoo.util.JwtUtil;
import com.zencoo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        logger.info("Login attempt for email: {}", email);

        boolean valid = authService.validateLogin(email, password);

        Map<String, Object> response = new HashMap<>();
        if (valid) {
            logger.info("Login successful for email: {}", email);
            User user = authService.getUserByEmail(email).get();
            String jwt = jwtUtil.generateToken(user.getId(), user.getEmail());
            response.put("token", jwt);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            logger.warn("Login failed for email: {}", email);
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String username = req.get("username");
        String password = req.get("password");
        String fullName = req.get("fullName");
        String doorNumber = req.get("doorNumber");
        String community = req.get("community");

        Map<String, Object> response = new HashMap<>();

        if (authService.isEmailRegistered(email)) {
            response.put("message", "Email already registered");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!authService.isUsernameUnique(username)) {
            response.put("message", "Username not available");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        authService.registerUser(email, username, password, fullName, doorNumber, community);
        response.put("message", "Registration successful");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = authService.isEmailRegistered(email);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("exists", exists);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean unique = authService.isUsernameUnique(username);
        Map<String, Boolean> resp = new HashMap<>();
        resp.put("unique", unique);
        return ResponseEntity.ok(resp);
    }

    // @PostMapping("/google-login")
    // public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
    //     String idToken = body.get("idToken");
    //     if (idToken == null) {
    //         return ResponseEntity.badRequest().body(Map.of("message", "Missing idToken"));
    //     }
    //
    //     GoogleIdToken.Payload payload = GoogleTokenVerifier.verify(idToken);
    //     if (payload == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid Google token"));
    //     }
    //
    //     String email = payload.getEmail();
    //     String name = (String) payload.get("name");
    //
    //     var userOpt = authService.getUserByEmail(email);
    //     if (userOpt.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "User not registered"));
    //     }
    //
    //     String jwt = JwtUtil.generateToken(email, name);
    //     var user = userOpt.get();
    //     return ResponseEntity.ok(Map.of(
    //         "jwt", jwt,
    //         "email", user.getEmail(),
    //         "name", user.getFullName() != null ? user.getFullName() : name
    //     ));
    // }
}