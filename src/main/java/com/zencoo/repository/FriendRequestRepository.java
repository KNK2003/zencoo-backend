package com.zencoo.repository;

import com.zencoo.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsBySenderIdAndReceiverIdAndStatusIn(Long senderId, Long receiverId, List<FriendRequest.Status> statuses);
    Optional<FriendRequest> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, FriendRequest.Status status);
    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendRequest.Status status);
    List<FriendRequest> findBySenderIdAndStatus(Long senderId, FriendRequest.Status status);
}