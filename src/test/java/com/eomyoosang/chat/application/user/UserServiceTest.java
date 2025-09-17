package com.eomyoosang.chat.application.user;

import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.presentation.user.dto.UpdateProfileRequest;
import com.eomyoosang.chat.presentation.user.dto.UserProfileDto;
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

@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UpdateProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = createUser("user123", "test@example.com", "testuser", true);
        updateRequest = new UpdateProfileRequest("newNickname", "새로운 상태메시지", "https://example.com/profile.jpg");
    }

    @Test
    @DisplayName("사용자 프로필 조회가 성공한다")
    void getUserProfileSuccess() {
        // given
        String userId = "user123";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserProfileDto result = userService.getUserProfile(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getEmail()).isNull(); // 공개 정보에는 이메일이 포함되지 않음
    }

    @Test
    @DisplayName("존재하지 않는 사용자 프로필 조회 시 예외가 발생한다")
    void getUserProfileWithNonExistentUserThrowsException() {
        // given
        String userId = "nonexistent";
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserProfile(userId))
            .isInstanceOf(UserException.UserNotFoundException.class);
    }

    @Test
    @DisplayName("내 프로필 조회가 성공하고 개인정보를 포함한다")
    void getMyProfileSuccess() {
        // given
        String userId = "user123";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserProfileDto result = userService.getMyProfile(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
        assertThat(result.getEmail()).isEqualTo(user.getEmail()); // 내 프로필에는 이메일 포함
    }

    @Test
    @DisplayName("프로필 업데이트가 성공한다")
    void updateProfileSuccess() {
        // given
        String userId = "user123";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("newNickname")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserProfileDto result = userService.updateProfile(userId, updateRequest);

        // then
        then(userRepository).should().save(user);
        assertThat(result.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 프로필 업데이트 시 예외가 발생한다")
    void updateProfileWithNonExistentUserThrowsException() {
        // given
        String userId = "nonexistent";
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(userId, updateRequest))
            .isInstanceOf(UserException.UserNotFoundException.class);
    }

    @Test
    @DisplayName("중복된 닉네임으로 프로필 업데이트 시 예외가 발생한다")
    void updateProfileWithDuplicateNicknameThrowsException() {
        // given
        String userId = "user123";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNickname("newNickname")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(userId, updateRequest))
            .isInstanceOf(UserException.NicknameAlreadyExistsException.class);
    }

    @Test
    @DisplayName("같은 닉네임으로 프로필 업데이트는 성공한다")
    void updateProfileWithSameNicknameSuccess() {
        // given
        String userId = "user123";
        UpdateProfileRequest sameNicknameRequest = new UpdateProfileRequest(
            "testuser", // 기존과 같은 닉네임
            "새로운 상태메시지",
            "https://example.com/profile.jpg"
        );
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserProfileDto result = userService.updateProfile(userId, sameNicknameRequest);

        // then
        then(userRepository).should().save(user);
        assertThat(result.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("이메일로 사용자 검색이 성공한다")
    void searchUsersByEmailSuccess() {
        // given
        String query = "test@example.com";
        String currentUserId = "current123";
        Pageable pageable = PageRequest.of(0, 10);

        User foundUser = createUser("found123", "test@example.com", "founduser", true);
        given(userRepository.findByEmail(query)).willReturn(Optional.of(foundUser));

        // when
        Page<UserProfileDto> result = userService.searchUsers(query, currentUserId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("found123");
    }

    @Test
    @DisplayName("닉네임으로 사용자 검색이 성공한다")
    void searchUsersByNicknameSuccess() {
        // given
        String query = "test";
        String currentUserId = "current123";
        Pageable pageable = PageRequest.of(0, 10);

        List<User> foundUsers = Arrays.asList(
            createUser("user1", "user1@example.com", "testuser1", true),
            createUser("user2", "user2@example.com", "testuser2", true)
        );
        given(userRepository.findByNicknameContaining(query)).willReturn(foundUsers);

        // when
        Page<UserProfileDto> result = userService.searchUsers(query, currentUserId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 검색 시 본인은 제외된다")
    void searchUsersExcludesCurrentUser() {
        // given
        String query = "test";
        String currentUserId = "user1";
        Pageable pageable = PageRequest.of(0, 10);

        List<User> foundUsers = Arrays.asList(
            createUser("user1", "user1@example.com", "testuser1", true), // 현재 사용자
            createUser("user2", "user2@example.com", "testuser2", true)
        );
        given(userRepository.findByNicknameContaining(query)).willReturn(foundUsers);

        // when
        Page<UserProfileDto> result = userService.searchUsers(query, currentUserId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("user2");
    }

    @Test
    @DisplayName("사용자 검색 시 비활성화된 사용자는 제외된다")
    void searchUsersExcludesInactiveUsers() {
        // given
        String query = "test";
        String currentUserId = "current123";
        Pageable pageable = PageRequest.of(0, 10);

        List<User> foundUsers = Arrays.asList(
            createUser("user1", "user1@example.com", "testuser1", true),  // 활성화
            createUser("user2", "user2@example.com", "testuser2", false)  // 비활성화
        );
        given(userRepository.findByNicknameContaining(query)).willReturn(foundUsers);

        // when
        Page<UserProfileDto> result = userService.searchUsers(query, currentUserId, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("user1");
    }

    @Test
    @DisplayName("페이징 처리가 올바르게 동작한다")
    void searchUsersWithPaginationWorksCorrectly() {
        // given
        String query = "test";
        String currentUserId = "current123";
        Pageable pageable = PageRequest.of(1, 2); // 2번째 페이지, 크기 2

        List<User> foundUsers = Arrays.asList(
            createUser("user1", "user1@example.com", "testuser1", true),
            createUser("user2", "user2@example.com", "testuser2", true),
            createUser("user3", "user3@example.com", "testuser3", true),
            createUser("user4", "user4@example.com", "testuser4", true),
            createUser("user5", "user5@example.com", "testuser5", true)
        );
        given(userRepository.findByNicknameContaining(query)).willReturn(foundUsers);

        // when
        Page<UserProfileDto> result = userService.searchUsers(query, currentUserId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("user3");
        assertThat(result.getContent().get(1).getUserId()).isEqualTo("user4");
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("이메일 존재 여부 확인이 정상 작동한다")
    void existsByEmailWorksCorrectly() {
        // given
        String email = "test@example.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when
        boolean exists = userService.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인이 정상 작동한다")
    void existsByNicknameWorksCorrectly() {
        // given
        String nickname = "testuser";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean exists = userService.existsByNickname(nickname);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("전화번호 존재 여부 확인이 정상 작동한다")
    void existsByPhoneWorksCorrectly() {
        // given
        String phone = "010-1234-5678";
        given(userRepository.existsByPhone(phone)).willReturn(true);

        // when
        boolean exists = userService.existsByPhone(phone);

        // then
        assertThat(exists).isTrue();
    }

    private User createUser(String id, String email, String nickname, boolean isActive) {
        User user = new User(id, email, "010-1234-5678", "password", nickname);
        if (!isActive) {
            user.deactivate();
        }
        return user;
    }
}