package com.eomyoosang.chat.presentation.user.dto;

import com.eomyoosang.chat.domain.user.entity.Friend;
import com.eomyoosang.chat.domain.user.entity.User;

import java.time.LocalDateTime;

public class FriendDto {
    private String userId;
    private String nickname;
    private String profileImageUrl;
    private String statusMessage;
    private Friend.FriendStatus status;
    private LocalDateTime friendsSince;

    public FriendDto() {}

    public FriendDto(String userId, String nickname, String profileImageUrl, String statusMessage,
                     Friend.FriendStatus status, LocalDateTime friendsSince) {
        this.userId = userId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.statusMessage = statusMessage;
        this.status = status;
        this.friendsSince = friendsSince;
    }

    public static FriendDto fromEntity(User user, Friend.FriendStatus status, LocalDateTime friendsSince) {
        return new FriendDto(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getStatusMessage(),
                status,
                friendsSince
        );
    }

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

    public Friend.FriendStatus getStatus() {
        return status;
    }

    public void setStatus(Friend.FriendStatus status) {
        this.status = status;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public void setFriendsSince(LocalDateTime friendsSince) {
        this.friendsSince = friendsSince;
    }
}