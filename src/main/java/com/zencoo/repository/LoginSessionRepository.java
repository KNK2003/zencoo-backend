package com.zencoo.repository;

import com.zencoo.model.LoginSession;
import com.zencoo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {
    Optional<LoginSession> findFirstByUserAndLogoutTimeIsNullOrderByLoginTimeDesc(User user);

    @Modifying
    @Query("UPDATE LoginSession ls SET ls.logoutTime = CURRENT_TIMESTAMP WHERE ls.user = :user AND ls.logoutTime IS NULL")
    int logoutOpenSessions(User user);
}