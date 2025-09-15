package com.eomyoosang.chat.domain.message.entity;

import com.eomyoosang.chat.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Column(name = "room_id", nullable = false, length = 26)
    private String roomId;

    @Column(name = "sender_id", nullable = false, length = 26)
    private String senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_id", length = 26)
    private String fileId;

    @Column(name = "read_count", nullable = false)
    private Integer readCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    protected Message() {
        // JPA를 위한 기본 생성자
    }

    public Message(String id, String roomId, String senderId, MessageType type, String content) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.readCount = 0;
        this.isDeleted = false;
    }

    public Message(String id, String roomId, String senderId, MessageType type, String content, String fileId) {
        this.id = id;
        this.roomId = roomId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.fileId = fileId;
        this.readCount = 0;
        this.isDeleted = false;
    }

    public String getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getFileId() {
        return fileId;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void updateContent(String content) {
        if (type == MessageType.TEXT) {
            this.content = content;
        }
    }

    public void incrementReadCount() {
        this.readCount++;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public boolean isTextMessage() {
        return type == MessageType.TEXT;
    }

    public boolean isMediaMessage() {
        return type == MessageType.IMAGE || type == MessageType.VIDEO || type == MessageType.FILE;
    }

    public enum MessageType {
        TEXT, IMAGE, FILE, VIDEO
    }
}