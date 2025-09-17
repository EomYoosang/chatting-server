package com.eomyoosang.chat.infrastructure.persistence.user;

import com.eomyoosang.chat.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserJpaRepository 테스트")
@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "user1@example.com", "010-1111-1111", "password1", "testuser1");
        user2 = new User("user2", "user2@example.com", "010-2222-2222", "password2", "testuser2");
        user3 = new User("user3", "user3@example.com", "010-3333-3333", "password3", "differentuser");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);
    }

    @Test
    @DisplayName("이메일로 사용자를 찾을 수 있다")
    void findByEmailSuccess() {
        // when
        Optional<User> found = userJpaRepository.findByEmail("user1@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("user1");
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
        assertThat(found.get().getNickname()).isEqualTo("testuser1");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 빈 Optional을 반환한다")
    void findByEmailNotFound() {
        // when
        Optional<User> found = userJpaRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("전화번호로 사용자를 찾을 수 있다")
    void findByPhoneSuccess() {
        // when
        Optional<User> found = userJpaRepository.findByPhone("010-2222-2222");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("user2");
        assertThat(found.get().getPhone()).isEqualTo("010-2222-2222");
    }

    @Test
    @DisplayName("존재하지 않는 전화번호로 조회 시 빈 Optional을 반환한다")
    void findByPhoneNotFound() {
        // when
        Optional<User> found = userJpaRepository.findByPhone("010-9999-9999");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("닉네임 부분 검색이 가능하다")
    void findByNicknameContainingSuccess() {
        // when
        List<User> found = userJpaRepository.findByNicknameContaining("testuser");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(User::getNickname)
                .containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    @DisplayName("닉네임 부분 검색에서 매치되지 않으면 빈 리스트를 반환한다")
    void findByNicknameContainingNotFound() {
        // when
        List<User> found = userJpaRepository.findByNicknameContaining("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("완전히 다른 닉네임도 부분 검색으로 찾을 수 있다")
    void findByNicknameContainingDifferentNickname() {
        // when
        List<User> found = userJpaRepository.findByNicknameContaining("different");

        // then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getNickname()).isEqualTo("differentuser");
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인할 수 있다")
    void existsByEmailTrue() {
        // when
        boolean exists = userJpaRepository.existsByEmail("user1@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일은 false를 반환한다")
    void existsByEmailFalse() {
        // when
        boolean exists = userJpaRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("전화번호 존재 여부를 확인할 수 있다")
    void existsByPhoneTrue() {
        // when
        boolean exists = userJpaRepository.existsByPhone("010-1111-1111");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 전화번호는 false를 반환한다")
    void existsByPhoneFalse() {
        // when
        boolean exists = userJpaRepository.existsByPhone("010-9999-9999");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("닉네임 존재 여부를 확인할 수 있다")
    void existsByNicknameTrue() {
        // when
        boolean exists = userJpaRepository.existsByNickname("testuser1");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 닉네임은 false를 반환한다")
    void existsByNicknameFalse() {
        // when
        boolean exists = userJpaRepository.existsByNickname("nonexistentuser");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("ID로 사용자를 찾을 수 있다")
    void findByIdSuccess() {
        // when
        Optional<User> found = userJpaRepository.findById("user1");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("user1");
        assertThat(found.get().getEmail()).isEqualTo("user1@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
    void findByIdNotFound() {
        // when
        Optional<User> found = userJpaRepository.findById("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("모든 사용자를 조회할 수 있다")
    void findAllSuccess() {
        // when
        List<User> allUsers = userJpaRepository.findAll();

        // then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getId)
                .containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @DisplayName("사용자를 저장할 수 있다")
    void saveUserSuccess() {
        // given
        User newUser = new User("user4", "user4@example.com", "010-4444-4444", "password4", "testuser4");

        // when
        User saved = userJpaRepository.save(newUser);

        // then
        assertThat(saved.getId()).isEqualTo("user4");
        assertThat(saved.getEmail()).isEqualTo("user4@example.com");

        // DB에서 다시 조회하여 확인
        Optional<User> found = userJpaRepository.findById("user4");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user4@example.com");
    }

    @Test
    @DisplayName("사용자를 수정할 수 있다")
    void updateUserSuccess() {
        // given
        User user = userJpaRepository.findById("user1").orElseThrow();

        // when
        user.updateProfile("updatedNickname", "새로운 상태메시지", "https://example.com/new-profile.jpg");
        User updated = userJpaRepository.save(user);

        // then
        assertThat(updated.getNickname()).isEqualTo("updatedNickname");

        // DB에서 다시 조회하여 확인
        User found = userJpaRepository.findById("user1").orElseThrow();
        assertThat(found.getNickname()).isEqualTo("updatedNickname");
    }

    @Test
    @DisplayName("사용자를 삭제할 수 있다")
    void deleteUserSuccess() {
        // given
        assertThat(userJpaRepository.existsById("user1")).isTrue();

        // when
        userJpaRepository.deleteById("user1");

        // then
        assertThat(userJpaRepository.existsById("user1")).isFalse();
        assertThat(userJpaRepository.findById("user1")).isEmpty();
    }

    @Test
    @DisplayName("전체 사용자 수를 확인할 수 있다")
    void countUsersSuccess() {
        // when
        long count = userJpaRepository.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("대소문자를 구분하여 이메일 검색이 이루어진다")
    void findByEmailCaseSensitive() {
        // when
        Optional<User> found = userJpaRepository.findByEmail("USER1@EXAMPLE.COM");

        // then
        assertThat(found).isEmpty(); // 대소문자가 다르므로 찾을 수 없음
    }

    @Test
    @DisplayName("닉네임 부분 검색에서 대소문자를 구분한다")
    void findByNicknameContainingCaseSensitive() {
        // when
        List<User> found = userJpaRepository.findByNicknameContaining("TESTUSER");

        // then
        assertThat(found).isEmpty(); // 대소문자가 다르므로 찾을 수 없음
    }
}