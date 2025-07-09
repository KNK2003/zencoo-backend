package com.zencoo.service;

import com.zencoo.model.LoginSession;
import com.zencoo.model.User;
import com.zencoo.repository.LoginSessionRepository;
import com.zencoo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoginSessionService {

    private static final Logger logger = LoggerFactory.getLogger(LoginSessionService.class);

    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Autowired
    private UserRepository userRepository;

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