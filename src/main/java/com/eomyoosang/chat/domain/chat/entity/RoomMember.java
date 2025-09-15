package com.eomyoosang.chat.domain.chat.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_members")
public class RoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false, length = 26)
    private String roomId;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    protected RoomMember() {
        // JPA를 위한 기본 생성자
    }

    public RoomMember(String roomId, String userId, MemberRole role) {
        this.roomId = roomId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
        this.isActive = true;
    }

    public RoomMember(String roomId, String userId) {
        this(roomId, userId, MemberRole.MEMBER);
    }

    public Long getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserId() {
        return userId;
    }

    public MemberRole getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void promoteToAdmin() {
        this.role = MemberRole.ADMIN;
    }

    public void demoteToMember() {
        this.role = MemberRole.MEMBER;
    }

    public void leave() {
        this.isActive = false;
        this.leftAt = LocalDateTime.now();
    }

    public void rejoin() {
        this.isActive = true;
        this.leftAt = null;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return role == MemberRole.ADMIN;
    }

    public boolean isMember() {
        return role == MemberRole.MEMBER;
    }

    public enum MemberRole {
        ADMIN, MEMBER
    }
}