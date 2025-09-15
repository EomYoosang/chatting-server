package com.eomyoosang.chat.domain.chat.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChatRoomType type;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", nullable = false, length = 26)
    private String createdBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    protected ChatRoom() {
        // JPA를 위한 기본 생성자
    }

    public ChatRoom(String id, ChatRoomType type, String createdBy) {
        this.id = id;
        this.type = type;
        this.createdBy = createdBy;
        this.isActive = true;
    }

    public ChatRoom(String id, ChatRoomType type, String name, String description, String createdBy) {
        this(id, type, createdBy);
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public ChatRoomType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void updateInfo(String name, String description) {
        if (type == ChatRoomType.GROUP) {
            this.name = name;
            this.description = description;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isDirect() {
        return type == ChatRoomType.DIRECT;
    }

    public boolean isGroup() {
        return type == ChatRoomType.GROUP;
    }

    public enum ChatRoomType {
        DIRECT, GROUP
    }
}