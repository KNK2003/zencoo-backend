package com.zencoo.service;

import com.zencoo.model.LoginSession;
import com.zencoo.model.User;
import com.zencoo.repository.LoginSessionRepository;
import com.zencoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginSessionService {

    private static final Logger logger = LoggerFactory.getLogger(LoginSessionService.class);

    private final LoginSessionRepository loginSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void recordLogin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LoginSession session = new LoginSession();
        session.setUser(user);
        session.setLoginTime(LocalDateTime.now());
        loginSessionRepository.save(session);
    }

    @Transactional
    public void recordLogout(Long userId) {
        logger.info("recordLogout called for userId={}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("No user found with id={}", userId);
            return;
        }
        Optional<LoginSession> sessionOpt = loginSessionRepository.findFirstByUserAndLogoutTimeIsNullOrderByLoginTimeDesc(user);
        if (sessionOpt.isPresent()) {
            LoginSession session = sessionOpt.get();
            session.setLogoutTime(java.time.LocalDateTime.now());
            loginSessionRepository.save(session);
            logger.info("Logout time set for session id={} (userId={})", session.getId(), userId);
        } else {
            logger.warn("No open login session found for userId={}", userId);
        }
    }
}