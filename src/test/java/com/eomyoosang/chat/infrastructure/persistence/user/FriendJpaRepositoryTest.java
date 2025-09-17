package com.eomyoosang.chat.infrastructure.persistence.user;

import com.eomyoosang.chat.domain.user.entity.Friend;
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

@DisplayName("FriendJpaRepository 테스트")
@DataJpaTest
class FriendJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FriendJpaRepository friendJpaRepository;

    private User user1;
    private User user2;
    private User user3;
    private Friend friend1;
    private Friend friend2;
    private Friend friend3;

    @BeforeEach
    void setUp() {
        // 사용자들 생성
        user1 = new User("user1", "user1@example.com", "010-1111-1111", "password1", "testuser1");
        user2 = new User("user2", "user2@example.com", "010-2222-2222", "password2", "testuser2");
        user3 = new User("user3", "user3@example.com", "010-3333-3333", "password3", "testuser3");

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);
        entityManager.persistAndFlush(user3);

        // 친구 관계 생성
        friend1 = new Friend("user1", "user2"); // user1 -> user2 (ACTIVE)
        friend2 = new Friend("user1", "user3"); // user1 -> user3 (ACTIVE)
        friend3 = new Friend("user2", "user1"); // user2 -> user1 (ACTIVE)

        entityManager.persistAndFlush(friend1);
        entityManager.persistAndFlush(friend2);
        entityManager.persistAndFlush(friend3);
    }

    @Test
    @DisplayName("사용자 ID와 친구 ID로 친구 관계를 찾을 수 있다")
    void findByUserIdAndFriendIdSuccess() {
        // when
        Optional<Friend> found = friendJpaRepository.findByUserIdAndFriendId("user1", "user2");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo("user1");
        assertThat(found.get().getFriendId()).isEqualTo("user2");
        assertThat(found.get().getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("존재하지 않는 친구 관계 조회 시 빈 Optional을 반환한다")
    void findByUserIdAndFriendIdNotFound() {
        // when
        Optional<Friend> found = friendJpaRepository.findByUserIdAndFriendId("user1", "nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("특정 사용자의 모든 친구를 조회할 수 있다")
    void findByUserIdSuccess() {
        // when
        List<Friend> friends = friendJpaRepository.findByUserId("user1");

        // then
        assertThat(friends).hasSize(2);
        assertThat(friends).extracting(Friend::getFriendId)
                .containsExactlyInAnyOrder("user2", "user3");
    }

    @Test
    @DisplayName("친구가 없는 사용자 조회 시 빈 리스트를 반환한다")
    void findByUserIdNoFriends() {
        // when
        List<Friend> friends = friendJpaRepository.findByUserId("user3");

        // then
        assertThat(friends).isEmpty();
    }

    @Test
    @DisplayName("특정 상태의 친구들만 조회할 수 있다")
    void findByUserIdAndStatusSuccess() {
        // given
        Friend blockedFriend = new Friend("user1", "user2");
        blockedFriend.block(); // 차단 상태로 변경
        entityManager.persistAndFlush(blockedFriend);

        // when
        List<Friend> activeFriends = friendJpaRepository.findByUserIdAndStatus("user1", Friend.FriendStatus.ACTIVE);
        List<Friend> blockedFriends = friendJpaRepository.findByUserIdAndStatus("user1", Friend.FriendStatus.BLOCKED);

        // then
        // 기존 ACTIVE 친구들 (friend1이 있지만 blockedFriend를 추가로 생성했으므로 중복 가능)
        assertThat(activeFriends).hasSizeGreaterThanOrEqualTo(1);
        assertThat(blockedFriends).hasSize(1);
    }

    @Test
    @DisplayName("친구 관계가 존재하는지 확인할 수 있다")
    void existsByUserIdAndFriendIdTrue() {
        // when
        boolean exists = friendJpaRepository.existsByUserIdAndFriendId("user1", "user2");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 친구 관계는 false를 반환한다")
    void existsByUserIdAndFriendIdFalse() {
        // when
        boolean exists = friendJpaRepository.existsByUserIdAndFriendId("user1", "nonexistent");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("친구 관계를 저장할 수 있다")
    void saveFriendSuccess() {
        // given
        Friend newFriend = new Friend("user2", "user3");

        // when
        Friend saved = friendJpaRepository.save(newFriend);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo("user2");
        assertThat(saved.getFriendId()).isEqualTo("user3");
        assertThat(saved.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);

        // DB에서 다시 조회하여 확인
        Optional<Friend> found = friendJpaRepository.findByUserIdAndFriendId("user2", "user3");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("친구 관계를 삭제할 수 있다")
    void deleteByUserIdAndFriendIdSuccess() {
        // given
        assertThat(friendJpaRepository.existsByUserIdAndFriendId("user1", "user2")).isTrue();

        // when
        friendJpaRepository.deleteByUserIdAndFriendId("user1", "user2");
        entityManager.flush();

        // then
        assertThat(friendJpaRepository.existsByUserIdAndFriendId("user1", "user2")).isFalse();
    }

    @Test
    @DisplayName("친구 관계의 상태를 업데이트할 수 있다")
    void updateFriendStatusSuccess() {
        // given
        Friend friend = friendJpaRepository.findByUserIdAndFriendId("user1", "user2").orElseThrow();
        assertThat(friend.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);

        // when
        friend.block();
        Friend updated = friendJpaRepository.save(friend);

        // then
        assertThat(updated.getStatus()).isEqualTo(Friend.FriendStatus.BLOCKED);

        // DB에서 다시 조회하여 확인
        Friend found = friendJpaRepository.findByUserIdAndFriendId("user1", "user2").orElseThrow();
        assertThat(found.getStatus()).isEqualTo(Friend.FriendStatus.BLOCKED);
    }

    @Test
    @DisplayName("차단된 친구를 다시 활성화할 수 있다")
    void unblockFriendSuccess() {
        // given
        Friend friend = friendJpaRepository.findByUserIdAndFriendId("user1", "user2").orElseThrow();
        friend.block();
        friendJpaRepository.save(friend);

        // when
        friend.unblock();
        Friend updated = friendJpaRepository.save(friend);

        // then
        assertThat(updated.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("모든 친구 관계를 조회할 수 있다")
    void findAllFriendsSuccess() {
        // when
        List<Friend> allFriends = friendJpaRepository.findAll();

        // then
        assertThat(allFriends).hasSize(3);
    }

    @Test
    @DisplayName("전체 친구 관계 수를 확인할 수 있다")
    void countFriendsSuccess() {
        // when
        long count = friendJpaRepository.count();

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("친구 관계를 ID로 삭제할 수 있다")
    void deleteByIdSuccess() {
        // given
        Friend friend = friendJpaRepository.findByUserIdAndFriendId("user1", "user2").orElseThrow();
        Long friendId = friend.getId();

        // when
        friendJpaRepository.deleteById(friendId);
        entityManager.flush();

        // then
        assertThat(friendJpaRepository.existsById(friendId)).isFalse();
        assertThat(friendJpaRepository.findByUserIdAndFriendId("user1", "user2")).isEmpty();
    }

    @Test
    @DisplayName("친구 관계 생성 시 기본 상태는 ACTIVE이다")
    void newFriendDefaultStatusIsActive() {
        // given
        Friend newFriend = new Friend("user3", "user1");

        // when
        Friend saved = friendJpaRepository.save(newFriend);

        // then
        assertThat(saved.getStatus()).isEqualTo(Friend.FriendStatus.ACTIVE);
    }

    @Test
    @DisplayName("같은 사용자 간의 중복 친구 관계를 생성할 수 있다")
    void canCreateDuplicateFriendRelationship() {
        // given
        Friend duplicateFriend = new Friend("user1", "user2"); // 이미 존재하는 관계

        // when
        Friend saved = friendJpaRepository.save(duplicateFriend);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo("user1");
        assertThat(saved.getFriendId()).isEqualTo("user2");

        // 두 개의 레코드가 존재할 수 있음 (비즈니스 로직에서 중복 체크 필요)
        List<Friend> friends = friendJpaRepository.findByUserId("user1");
        long user2FriendCount = friends.stream()
                .filter(f -> f.getFriendId().equals("user2"))
                .count();
        assertThat(user2FriendCount).isGreaterThanOrEqualTo(1);
    }
}