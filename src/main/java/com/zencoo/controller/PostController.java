package com.zencoo.controller;

import com.zencoo.dto.PostDto;
import com.zencoo.model.Post;
import com.zencoo.model.User;
import com.zencoo.repository.PostRepository;
import com.zencoo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        String imageUrl = payload.get("imageUrl");
        String caption = payload.get("caption");
        if (userId == null || imageUrl == null) {
            return ResponseEntity.badRequest().body("Missing user or imageUrl");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Post post = new Post();
        post.setUser(user);
        post.setImageUrl(imageUrl);
        post.setCaption(caption);
        postRepository.save(post);

        return ResponseEntity.ok(Map.of("success", true, "postId", post.getId()));
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(PostDto::new)
                .toList();
    }

    @GetMapping("/user/{userId}")
    public List<PostDto> getPostsByUser(@PathVariable Long userId) {
        return postRepository.findByUserId(userId)
                .stream()
                .map(PostDto::new)
                .toList();
    }
}