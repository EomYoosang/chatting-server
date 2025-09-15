package com.eomyoosang.chat.domain.chat.repository;

import com.eomyoosang.chat.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository {
    ChatRoom save(ChatRoom chatRoom);
    Optional<ChatRoom> findById(String id);
    List<ChatRoom> findByCreatedBy(String createdBy);
    List<ChatRoom> findByType(ChatRoom.ChatRoomType type);
    List<ChatRoom> findByIsActive(Boolean isActive);
    List<ChatRoom> findAll();
    void deleteById(String id);
}