package com.eomyoosang.chat.presentation.user.dto;

import com.eomyoosang.chat.domain.user.entity.User;

import java.time.LocalDateTime;

public class UserProfileDto {
    private String userId;
    private String nickname;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String statusMessage;
    private LocalDateTime createdAt;

    public UserProfileDto() {}

    public UserProfileDto(String userId, String nickname, String email, String phone,
                         String profileImageUrl, String statusMessage, LocalDateTime createdAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.statusMessage = statusMessage;
        this.createdAt = createdAt;
    }

    public static UserProfileDto fromEntity(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getNickname(),
                null, // 다른 사용자 프로필 조회시 이메일 숨김
                null, // 다른 사용자 프로필 조회시 전화번호 숨김
                user.getProfileImageUrl(),
                user.getStatusMessage(),
                user.getCreatedAt()
        );
    }

    public static UserProfileDto fromEntityWithPrivateInfo(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getProfileImageUrl(),
                user.getStatusMessage(),
                user.getCreatedAt()
        );
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}