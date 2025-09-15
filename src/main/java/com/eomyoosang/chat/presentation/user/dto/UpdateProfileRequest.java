package com.eomyoosang.chat.presentation.user.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "Nickname must be between 1 and 50 characters")
    private String nickname;

    @Size(max = 200, message = "Status message must be less than 200 characters")
    private String statusMessage;

    private String profileImageUrl;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String nickname, String statusMessage, String profileImageUrl) {
        this.nickname = nickname;
        this.statusMessage = statusMessage;
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}