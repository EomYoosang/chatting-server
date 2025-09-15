package com.eomyoosang.chat.domain.user.repository;

import com.eomyoosang.chat.domain.user.entity.Friend;

import java.util.List;
import java.util.Optional;

public interface FriendRepository {
    Friend save(Friend friend);
    Optional<Friend> findByUserIdAndFriendId(String userId, String friendId);
    List<Friend> findByUserIdAndStatus(String userId, Friend.FriendStatus status);
    List<Friend> findByUserId(String userId);
    void deleteByUserIdAndFriendId(String userId, String friendId);
    boolean existsByUserIdAndFriendId(String userId, String friendId);
}