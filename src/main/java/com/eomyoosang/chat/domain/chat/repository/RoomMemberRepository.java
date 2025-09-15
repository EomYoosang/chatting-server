package com.eomyoosang.chat.domain.chat.repository;

import com.eomyoosang.chat.domain.chat.entity.RoomMember;

import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository {
    RoomMember save(RoomMember roomMember);
    Optional<RoomMember> findByRoomIdAndUserId(String roomId, String userId);
    List<RoomMember> findByRoomIdAndIsActive(String roomId, Boolean isActive);
    List<RoomMember> findByUserIdAndIsActive(String userId, Boolean isActive);
    List<RoomMember> findByRoomId(String roomId);
    List<RoomMember> findByUserId(String userId);
    long countByRoomIdAndIsActive(String roomId, Boolean isActive);
    void deleteById(Long id);
}