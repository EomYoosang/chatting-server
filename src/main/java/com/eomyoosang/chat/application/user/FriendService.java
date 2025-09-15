package com.eomyoosang.chat.application.user;

import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import com.eomyoosang.chat.domain.user.entity.Friend;
import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.exception.FriendException;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.domain.user.repository.FriendRepository;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.presentation.user.dto.AddFriendRequest;
import com.eomyoosang.chat.presentation.user.dto.FriendDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    public Page<FriendDto> getFriends(String userId, Friend.FriendStatus status, Pageable pageable) {
        List<Friend> friends;
        if (status != null) {
            friends = friendRepository.findByUserIdAndStatus(userId, status);
        } else {
            friends = friendRepository.findByUserId(userId);
        }

        // Friend 엔티티에서 실제 사용자 정보를 가져와 FriendDto로 변환
        List<String> friendIds = friends.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());

        List<User> friendUsers = friendIds.stream()
                .map(friendId -> userRepository.findById(friendId).orElse(null))
                .filter(user -> user != null && user.getIsActive())
                .collect(Collectors.toList());

        // Friend 상태 정보를 매핑
        Map<String, Friend.FriendStatus> friendStatusMap = friends.stream()
                .collect(Collectors.toMap(Friend::getFriendId, Friend::getStatus));

        List<FriendDto> friendDtos = friendUsers.stream()
                .map(user -> FriendDto.fromEntity(
                        user,
                        friendStatusMap.get(user.getId()),
                        friends.stream()
                                .filter(f -> f.getFriendId().equals(user.getId()))
                                .findFirst()
                                .map(f -> f.getCreatedAt())
                                .orElse(null)
                ))
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), friendDtos.size());

        if (start >= friendDtos.size()) {
            return new PageImpl<>(List.of(), pageable, friendDtos.size());
        }

        return new PageImpl<>(
            friendDtos.subList(start, end),
            pageable,
            friendDtos.size()
        );
    }

    @Transactional
    public FriendDto addFriend(String userId, AddFriendRequest request) {
        // 자기 자신 추가 방지
        if (userId.equals(request.getFriendId()) ||
            (request.getEmail() != null && userRepository.findById(userId)
                .map(User::getEmail)
                .filter(email -> email.equals(request.getEmail()))
                .isPresent())) {
            throw new UserException.CannotAddYourselfAsFriendException();
        }

        // 친구로 추가할 사용자 찾기
        User friendUser;
        if (request.getFriendId() != null) {
            friendUser = userRepository.findById(request.getFriendId())
                    .orElseThrow(() -> new UserException.UserNotFoundException());
        } else if (request.getEmail() != null) {
            friendUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserException.UserNotFoundException());
        } else {
            throw new UserException(ErrorCode.INVALID_REQUEST, "Friend ID or email is required");
        }

        // 이미 친구인지 확인
        if (friendRepository.existsByUserIdAndFriendId(userId, friendUser.getId())) {
            throw new FriendException.AlreadyFriendsException();
        }

        // 양방향 친구 관계 생성
        Friend friend1 = new Friend(userId, friendUser.getId());
        Friend friend2 = new Friend(friendUser.getId(), userId);

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        return FriendDto.fromEntity(friendUser, Friend.FriendStatus.ACTIVE, friend1.getCreatedAt());
    }

    @Transactional
    public FriendDto updateFriendStatus(String userId, String friendId, Friend.FriendStatus status) {
        Friend friend = friendRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseThrow(() -> new FriendException.FriendRelationshipNotFoundException());

        User friendUser = userRepository.findById(friendId)
                .orElseThrow(() -> new UserException.UserNotFoundException());

        if (status == Friend.FriendStatus.BLOCKED) {
            friend.block();
        } else if (status == Friend.FriendStatus.ACTIVE) {
            friend.unblock();
        }
        Friend savedFriend = friendRepository.save(friend);

        return FriendDto.fromEntity(friendUser, savedFriend.getStatus(), savedFriend.getCreatedAt());
    }

    @Transactional
    public void deleteFriend(String userId, String friendId) {
        if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
            throw new FriendException.FriendRelationshipNotFoundException();
        }

        // 양방향 친구 관계 삭제
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
        friendRepository.deleteByUserIdAndFriendId(friendId, userId);
    }

    public Optional<Friend.FriendStatus> getFriendStatus(String userId, String friendId) {
        return friendRepository.findByUserIdAndFriendId(userId, friendId)
                .map(Friend::getStatus);
    }

    public boolean areFriends(String userId, String friendId) {
        return friendRepository.existsByUserIdAndFriendId(userId, friendId);
    }
}