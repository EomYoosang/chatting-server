package com.eomyoosang.chat.domain.message.repository;

import com.eomyoosang.chat.domain.message.entity.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(String id);
    List<Message> findByRoomIdAndIsDeleted(String roomId, Boolean isDeleted);
    List<Message> findByRoomIdAndCreatedAtAfter(String roomId, LocalDateTime after);
    List<Message> findByRoomIdOrderByCreatedAtDesc(String roomId);
    List<Message> findByRoomIdAndContentContaining(String roomId, String content);
    long countByRoomIdAndIsDeleted(String roomId, Boolean isDeleted);
    void deleteById(String id);
}