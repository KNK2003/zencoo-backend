package com.zencoo.controller;

import com.zencoo.dto.FriendDto;
import com.zencoo.dto.FriendRequestDto;
import com.zencoo.model.FriendRequest;
import com.zencoo.model.User;
import com.zencoo.repository.FriendRequestRepository;
import com.zencoo.repository.UserRepository;
import com.zencoo.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(@RequestBody FriendRequestDto dto) {
        String result = friendService.sendFriendRequest(dto.getSenderId(), dto.getReceiverId());
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/accept")
    public ResponseEntity<?> accept(@RequestBody Map<String, Long> body) {
        String result = friendService.acceptFriendRequest(body.get("requestId"));
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/decline")
    public ResponseEntity<?> decline(@RequestBody Map<String, Long> body) {
        String result = friendService.declineFriendRequest(body.get("requestId"));
        return ResponseEntity.ok(Map.of("message", result));
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<?> unfriend(@RequestBody Map<String, Long> body) {
        String result = friendService.unfriend(body.get("userA"), body.get("userB"));
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<FriendDto>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(friendService.getFriendsList(userId));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(@RequestParam Long userA, @RequestParam Long userB) {
        String status = friendService.getRelationshipStatus(userA, userB);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/pending-request")
    public ResponseEntity<?> getPendingRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        Optional<FriendRequest> req = friendRequestRepository.findBySenderIdAndReceiverIdAndStatus(
            senderId, receiverId, FriendRequest.Status.PENDING
        );
        return req.map(r -> ResponseEntity.ok(Map.of("id", r.getId())))
                  .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/requests/incoming/{userId}")
    public ResponseEntity<List<FriendRequestDto>> getIncomingRequests(@PathVariable Long userId) {
        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(
            userId, FriendRequest.Status.PENDING
        );
        List<FriendRequestDto> dtos = requests.stream()
            .map(r -> {
                User sender = userRepository.findById(r.getSenderId()).orElse(null);
                if (sender == null) return null;
                return FriendRequestDto.builder()
                    .id(r.getId())
                    .senderId(sender.getId())
                    .receiverId(r.getReceiverId()) 
                    .displayName(sender.getFullName())
                    .username(sender.getUsername())
                    .door(sender.getDoorNumber())
                    .wing(sender.getCommunity())
                    .profilePic(sender.getProfilePic())
                    .timestamp(r.getTimestamp() != null ? r.getTimestamp().toString() : null)
                    .build();
            })
            .filter(Objects::nonNull)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Map<String, Integer>> getFriendsCount(@PathVariable Long userId) {
        int count = friendService.getFriendsCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}