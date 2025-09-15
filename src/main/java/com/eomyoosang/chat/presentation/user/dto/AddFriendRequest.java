package com.eomyoosang.chat.presentation.user.dto;

import jakarta.validation.constraints.Email;

public class AddFriendRequest {

    private String friendId;

    @Email(message = "Valid email format required")
    private String email;

    public AddFriendRequest() {}

    public AddFriendRequest(String friendId, String email) {
        this.friendId = friendId;
        this.email = email;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}