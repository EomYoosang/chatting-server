package com.eomyoosang.chat.infrastructure.persistence.user;

import com.eomyoosang.chat.domain.user.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendJpaRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByUserIdAndFriendId(String userId, String friendId);
    List<Friend> findByUserIdAndStatus(String userId, Friend.FriendStatus status);
    List<Friend> findByUserId(String userId);
    void deleteByUserIdAndFriendId(String userId, String friendId);
    boolean existsByUserIdAndFriendId(String userId, String friendId);
}