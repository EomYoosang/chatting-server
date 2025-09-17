package com.eomyoosang.chat.application.user;

import com.eomyoosang.chat.domain.user.entity.Friend;
import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.exception.FriendException;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.domain.user.repository.FriendRepository;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.presentation.user.dto.AddFriendRequest;
import com.eomyoosang.chat.presentation.user.dto.FriendDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DisplayName("FriendService 테스트")
@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private User user;
    private User friendUser;
    private Friend friend;

    @BeforeEach
    void setUp() {
        user = createUser("user123", "user@example.com", "user");
        friendUser = createUser("friend123", "friend@example.com", "friend");
        friend = createFriend("user123", "friend123", Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("친구 목록 조회가 성공한다")
    void getFriendsSuccess() {
        // given
        String userId = "user123";
        Pageable pageable = PageRequest.of(0, 10);

        List<Friend> friends = Arrays.asList(friend);
        given(friendRepository.findByUserId(userId)).willReturn(friends);
        given(userRepository.findById("friend123")).willReturn(Optional.of(friendUser));

        // when
        Page<FriendDto> result = friendService.getFriends(userId, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("friend123");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("특정 상태의 친구 목록 조회가 성공한다")
    void getFriendsWithStatusSuccess() {
        // given
        String userId = "user123";
        Friend.FriendStatus status = Friend.FriendStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);

        List<Friend> friends = Arrays.asList(friend);
        given(friendRepository.findByUserIdAndStatus(userId, status)).willReturn(friends);
        given(userRepository.findById("friend123")).willReturn(Optional.of(friendUser));

        // when
        Page<FriendDto> result = friendService.getFriends(userId, status, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("비활성화된 친구는 목록에서 제외된다")
    void getFriendsExcludesInactiveUsers() {
        // given
        String userId = "user123";
        Pageable pageable = PageRequest.of(0, 10);

        User inactiveFriendUser = createInactiveUser("inactive123", "inactive@example.com", "inactive");
        List<Friend> friends = Arrays.asList(friend);
        given(friendRepository.findByUserId(userId)).willReturn(friends);
        given(userRepository.findById("friend123")).willReturn(Optional.of(inactiveFriendUser));

        // when
        Page<FriendDto> result = friendService.getFriends(userId, null, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("친구 ID로 친구 추가가 성공한다")
    void addFriendByIdSuccess() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setFriendId("friend123");

        given(userRepository.findById("friend123")).willReturn(Optional.of(friendUser));
        given(friendRepository.existsByUserIdAndFriendId(userId, "friend123")).willReturn(false);
        given(friendRepository.save(any(Friend.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        FriendDto result = friendService.addFriend(userId, request);

        // then
        assertThat(result.getUserId()).isEqualTo("friend123");
        assertThat(result.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
        then(friendRepository).should(times(2)).save(any(Friend.class)); // 양방향 저장
    }

    @Test
    @DisplayName("이메일로 친구 추가가 성공한다")
    void addFriendByEmailSuccess() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setEmail("friend@example.com");

        given(userRepository.findByEmail("friend@example.com")).willReturn(Optional.of(friendUser));
        given(friendRepository.existsByUserIdAndFriendId(userId, "friend123")).willReturn(false);
        given(friendRepository.save(any(Friend.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        FriendDto result = friendService.addFriend(userId, request);

        // then
        assertThat(result.getUserId()).isEqualTo("friend123");
        assertThat(result.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
        then(friendRepository).should(times(2)).save(any(Friend.class)); // 양방향 저장
    }

    @Test
    @DisplayName("자기 자신을 친구로 추가 시 예외가 발생한다")
    void addFriendToSelfThrowsException() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setFriendId("user123");

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(userId, request))
            .isInstanceOf(UserException.CannotAddYourselfAsFriendException.class);
    }

    @Test
    @DisplayName("자신의 이메일로 친구 추가 시 예외가 발생한다")
    void addFriendByOwnEmailThrowsException() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setEmail("user@example.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(userId, request))
            .isInstanceOf(UserException.CannotAddYourselfAsFriendException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 친구로 추가 시 예외가 발생한다")
    void addNonExistentFriendThrowsException() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setFriendId("nonexistent");

        given(userRepository.findById("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(userId, request))
            .isInstanceOf(UserException.UserNotFoundException.class);
    }

    @Test
    @DisplayName("이미 친구인 사용자를 추가 시 예외가 발생한다")
    void addAlreadyFriendThrowsException() {
        // given
        String userId = "user123";
        AddFriendRequest request = new AddFriendRequest();
        request.setFriendId("friend123");

        given(userRepository.findById("friend123")).willReturn(Optional.of(friendUser));
        given(friendRepository.existsByUserIdAndFriendId(userId, "friend123")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.addFriend(userId, request))
            .isInstanceOf(FriendException.AlreadyFriendsException.class);
    }

    @Test
    @DisplayName("친구 상태 업데이트가 성공한다 - 차단")
    void updateFriendStatusToBlockedSuccess() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.findByUserIdAndFriendId(userId, friendId)).willReturn(Optional.of(friend));
        given(userRepository.findById(friendId)).willReturn(Optional.of(friendUser));
        given(friendRepository.save(any(Friend.class))).willReturn(friend);

        // when
        FriendDto result = friendService.updateFriendStatus(userId, friendId, Friend.FriendStatus.BLOCKED);

        // then
        assertThat(result.getUserId()).isEqualTo(friendId);
        then(friendRepository).should().save(friend);
    }

    @Test
    @DisplayName("친구 상태 업데이트가 성공한다 - 차단 해제")
    void updateFriendStatusToActiveSuccess() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.findByUserIdAndFriendId(userId, friendId)).willReturn(Optional.of(friend));
        given(userRepository.findById(friendId)).willReturn(Optional.of(friendUser));
        given(friendRepository.save(any(Friend.class))).willReturn(friend);

        // when
        FriendDto result = friendService.updateFriendStatus(userId, friendId, Friend.FriendStatus.ACTIVE);

        // then
        assertThat(result.getUserId()).isEqualTo(friendId);
        then(friendRepository).should().save(friend);
    }

    @Test
    @DisplayName("존재하지 않는 친구 관계 업데이트 시 예외가 발생한다")
    void updateNonExistentFriendStatusThrowsException() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.findByUserIdAndFriendId(userId, friendId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> friendService.updateFriendStatus(userId, friendId, Friend.FriendStatus.BLOCKED))
            .isInstanceOf(FriendException.FriendRelationshipNotFoundException.class);
    }

    @Test
    @DisplayName("친구 삭제가 성공한다")
    void deleteFriendSuccess() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.existsByUserIdAndFriendId(userId, friendId)).willReturn(true);

        // when
        friendService.deleteFriend(userId, friendId);

        // then
        then(friendRepository).should().deleteByUserIdAndFriendId(userId, friendId);
        then(friendRepository).should().deleteByUserIdAndFriendId(friendId, userId); // 양방향 삭제
    }

    @Test
    @DisplayName("존재하지 않는 친구 관계 삭제 시 예외가 발생한다")
    void deleteNonExistentFriendThrowsException() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.existsByUserIdAndFriendId(userId, friendId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> friendService.deleteFriend(userId, friendId))
            .isInstanceOf(FriendException.FriendRelationshipNotFoundException.class);
    }

    @Test
    @DisplayName("친구 상태 조회가 성공한다")
    void getFriendStatusSuccess() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.findByUserIdAndFriendId(userId, friendId)).willReturn(Optional.of(friend));

        // when
        Optional<Friend.FriendStatus> result = friendService.getFriendStatus(userId, friendId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("친구 관계 확인이 정상 작동한다")
    void areFriendsWorksCorrectly() {
        // given
        String userId = "user123";
        String friendId = "friend123";

        given(friendRepository.existsByUserIdAndFriendId(userId, friendId)).willReturn(true);

        // when
        boolean result = friendService.areFriends(userId, friendId);

        // then
        assertThat(result).isTrue();
    }

    private User createUser(String id, String email, String nickname) {
        return new User(id, email, "010-1234-5678", "password", nickname);
    }

    private User createInactiveUser(String id, String email, String nickname) {
        User user = new User(id, email, "010-1234-5678", "password", nickname);
        user.deactivate();
        return user;
    }

    private Friend createFriend(String userId, String friendId, Friend.FriendStatus status) {
        Friend friend = new Friend(userId, friendId);
        if (status == Friend.FriendStatus.BLOCKED) {
            friend.block();
        }
        return friend;
    }
}