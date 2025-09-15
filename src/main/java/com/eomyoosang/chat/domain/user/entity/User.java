package com.eomyoosang.chat.domain.user.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Email
    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url", columnDefinition = "TEXT")
    private String profileImageUrl;

    @Size(max = 200)
    @Column(name = "status_message", length = 200)
    private String statusMessage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    protected User() {
        // JPA를 위한 기본 생성자
    }

    public User(String id, String email, String phone, String passwordHash, String nickname) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void updateProfile(String nickname, String statusMessage, String profileImageUrl) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname.trim();
        }
        this.statusMessage = statusMessage;
        this.profileImageUrl = profileImageUrl;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}