package com.eomyoosang.chat.infrastructure.persistence.user;

import com.eomyoosang.chat.domain.user.entity.Friend;
import com.eomyoosang.chat.domain.user.repository.FriendRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FriendRepositoryImpl implements FriendRepository {

    private final FriendJpaRepository friendJpaRepository;

    public FriendRepositoryImpl(FriendJpaRepository friendJpaRepository) {
        this.friendJpaRepository = friendJpaRepository;
    }

    @Override
    public Friend save(Friend friend) {
        return friendJpaRepository.save(friend);
    }

    @Override
    public Optional<Friend> findByUserIdAndFriendId(String userId, String friendId) {
        return friendJpaRepository.findByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public List<Friend> findByUserIdAndStatus(String userId, Friend.FriendStatus status) {
        return friendJpaRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<Friend> findByUserId(String userId) {
        return friendJpaRepository.findByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndFriendId(String userId, String friendId) {
        friendJpaRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    @Override
    public boolean existsByUserIdAndFriendId(String userId, String friendId) {
        return friendJpaRepository.existsByUserIdAndFriendId(userId, friendId);
    }
}