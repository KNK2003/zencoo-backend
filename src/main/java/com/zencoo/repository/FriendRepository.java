package com.zencoo.repository;

import com.zencoo.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    boolean existsByUserId1AndUserId2(Long userId1, Long userId2);
    Optional<Friend> findByUserId1AndUserId2(Long userId1, Long userId2);
    List<Friend> findByUserId1OrUserId2(Long userId1, Long userId2);
    void deleteByUserId1AndUserId2(Long userId1, Long userId2);

    @Query("SELECT COUNT(f) FROM Friend f WHERE f.userId1 = :userId OR f.userId2 = :userId")
    int countMutualFriends(@Param("userId") Long userId);
}