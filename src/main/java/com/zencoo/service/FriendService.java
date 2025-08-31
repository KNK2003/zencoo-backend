package com.zencoo.service;

import com.zencoo.dto.FriendDto;
import com.zencoo.model.Friend;
import com.zencoo.model.FriendRequest;
import com.zencoo.model.User;
import com.zencoo.repository.FriendRepository;
import com.zencoo.repository.FriendRequestRepository;
import com.zencoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public boolean areFriends(Long userA, Long userB) {
        return friendRepository.existsByUserId1AndUserId2(userA, userB) ||
               friendRepository.existsByUserId1AndUserId2(userB, userA);
    }

    public String getRelationshipStatus(Long userA, Long userB) {
        if (areFriends(userA, userB)) return "FRIENDS";
        Optional<FriendRequest> sent = friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(userA, userB, FriendRequest.Status.PENDING);
        if (sent.isPresent()) return "REQUEST_SENT";
        Optional<FriendRequest> received = friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(userB, userA, FriendRequest.Status.PENDING);
        if (received.isPresent()) return "REQUEST_RECEIVED";
        return "NOT_FRIENDS";
    }

    @Transactional
    public String sendFriendRequest(Long senderId, Long receiverId) {
        if (areFriends(senderId, receiverId)) return "Already friends";
        if (friendRequestRepository.existsBySenderIdAndReceiverIdAndStatusIn(senderId, receiverId, Arrays.asList(FriendRequest.Status.PENDING, FriendRequest.Status.ACCEPTED)))
            return "Request already sent or already friends";
        FriendRequest request = FriendRequest.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .status(FriendRequest.Status.PENDING)
                .timestamp(LocalDateTime.now())
                .build();
        friendRequestRepository.save(request);
        return "Request sent";
    }

    @Transactional
    public String acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));
        if (request.getStatus() != FriendRequest.Status.PENDING) return "Request not pending";
        request.setStatus(FriendRequest.Status.ACCEPTED);
        friendRequestRepository.save(request);

        // Add to friends table (both directions)
        Friend friend = Friend.builder()
                .userId1(request.getSenderId())
                .userId2(request.getReceiverId())
                .since(LocalDateTime.now())
                .build();
        friendRepository.save(friend);

        // Update friends_count for both users
        userRepository.incrementFriendsCount(request.getSenderId());
        userRepository.incrementFriendsCount(request.getReceiverId());

        return "Friend request accepted";
    }

    @Transactional
    public String declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));
        if (request.getStatus() != FriendRequest.Status.PENDING) return "Request not pending";
        request.setStatus(FriendRequest.Status.DECLINED);
        friendRequestRepository.save(request);
        return "Friend request declined";
    }

    @Transactional
    public String unfriend(Long userA, Long userB) {
        Optional<Friend> f1 = friendRepository.findByUserId1AndUserId2(userA, userB);
        Optional<Friend> f2 = friendRepository.findByUserId1AndUserId2(userB, userA);
        f1.ifPresent(friendRepository::delete);
        f2.ifPresent(friendRepository::delete);
        userRepository.decrementFriendsCount(userA);
        userRepository.decrementFriendsCount(userB);
        return "Unfriended";
    }

    public List<FriendDto> getFriendsList(Long userId) {
        List<Friend> friends = friendRepository.findByUserId1OrUserId2(userId, userId);
        Set<Long> friendIds = new HashSet<>();
        for (Friend f : friends) {
            if (f.getUserId1().equals(userId)) friendIds.add(f.getUserId2());
            else friendIds.add(f.getUserId1());
        }
        List<User> users = userRepository.findAllById(friendIds);
        return users.stream()
                .map(u -> new FriendDto(u.getId(), u.getUsername(), u.getFullName(), u.getProfilePic()))
                .collect(Collectors.toList());
    }

    public int getFriendsCount(Long userId) {
        return friendRepository.countMutualFriends(userId);
    }
}