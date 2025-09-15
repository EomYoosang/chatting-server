package com.eomyoosang.chat.domain.user.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "friend_id", nullable = false, length = 26)
    private String friendId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status = FriendStatus.ACTIVE;

    protected Friend() {
        // JPA를 위한 기본 생성자
    }

    public Friend(String userId, String friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = FriendStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void block() {
        this.status = FriendStatus.BLOCKED;
    }

    public void unblock() {
        this.status = FriendStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == FriendStatus.ACTIVE;
    }

    public boolean isBlocked() {
        return status == FriendStatus.BLOCKED;
    }

    public enum FriendStatus {
        ACTIVE, BLOCKED
    }
}