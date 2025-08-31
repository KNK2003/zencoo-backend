package com.zencoo.repository;

import com.zencoo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.friendsCount = u.friendsCount + 1 WHERE u.id = :userId")
    void incrementFriendsCount(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.friendsCount = u.friendsCount - 1 WHERE u.id = :userId AND u.friendsCount > 0")
    void decrementFriendsCount(Long userId);
}
