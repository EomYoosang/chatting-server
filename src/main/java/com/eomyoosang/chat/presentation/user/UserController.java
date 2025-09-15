package com.eomyoosang.chat.presentation.user;

import com.eomyoosang.chat.application.user.FriendService;
import com.eomyoosang.chat.application.user.UserService;
import com.eomyoosang.chat.domain.user.entity.Friend;
import com.eomyoosang.chat.presentation.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FriendService friendService;

    public UserController(UserService userService, FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(Authentication authentication) {
        String userId = authentication.getName();
        UserProfileDto profile = userService.getMyProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String userId) {
        UserProfileDto profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        UserProfileDto updatedProfile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserProfileDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String currentUserId = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfileDto> users = userService.searchUsers(query, currentUserId, pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/friends")
    public ResponseEntity<Page<FriendDto>> getFriends(
            @RequestParam(required = false) Friend.FriendStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        String userId = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<FriendDto> friends = friendService.getFriends(userId, status, pageable);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/friends")
    public ResponseEntity<FriendDto> addFriend(
            @Valid @RequestBody AddFriendRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        FriendDto friend = friendService.addFriend(userId, request);
        return ResponseEntity.ok(friend);
    }

    @PutMapping("/friends/{friendId}/status")
    public ResponseEntity<FriendDto> updateFriendStatus(
            @PathVariable String friendId,
            @RequestParam Friend.FriendStatus status,
            Authentication authentication) {
        String userId = authentication.getName();
        FriendDto friend = friendService.updateFriendStatus(userId, friendId, status);
        return ResponseEntity.ok(friend);
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable String friendId,
            Authentication authentication) {
        String userId = authentication.getName();
        friendService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friends/{friendId}/status")
    public ResponseEntity<Friend.FriendStatus> getFriendStatus(
            @PathVariable String friendId,
            Authentication authentication) {
        String userId = authentication.getName();
        return friendService.getFriendStatus(userId, friendId)
                .map(status -> ResponseEntity.ok(status))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/validation/email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validation/nickname")
    public ResponseEntity<Boolean> checkNicknameExists(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/validation/phone")
    public ResponseEntity<Boolean> checkPhoneExists(@RequestParam String phone) {
        boolean exists = userService.existsByPhone(phone);
        return ResponseEntity.ok(exists);
    }
}