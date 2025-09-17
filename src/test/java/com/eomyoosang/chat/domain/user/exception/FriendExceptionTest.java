package com.eomyoosang.chat.domain.user.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FriendException 테스트")
class FriendExceptionTest {

    @Test
    @DisplayName("FriendException은 BusinessException을 상속한다")
    void friendExceptionExtendsBusinessException() {
        // given & when
        FriendException exception = new FriendException(ErrorCode.FRIEND_NOT_FOUND);

        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("FriendNotFoundException은 올바른 ErrorCode와 메시지를 가진다")
    void friendNotFoundExceptionHasCorrectErrorCode() {
        // given & when
        FriendException.FriendNotFoundException exception = new FriendException.FriendNotFoundException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FRIEND_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.FRIEND_NOT_FOUND.getMessage());
        assertThat(exception).isInstanceOf(FriendException.class);
    }

    @Test
    @DisplayName("AlreadyFriendsException은 올바른 ErrorCode와 메시지를 가진다")
    void alreadyFriendsExceptionHasCorrectErrorCode() {
        // given & when
        FriendException.AlreadyFriendsException exception = new FriendException.AlreadyFriendsException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FRIENDS);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.ALREADY_FRIENDS.getMessage());
        assertThat(exception).isInstanceOf(FriendException.class);
    }

    @Test
    @DisplayName("FriendRelationshipNotFoundException은 올바른 ErrorCode와 메시지를 가진다")
    void friendRelationshipNotFoundExceptionHasCorrectErrorCode() {
        // given & when
        FriendException.FriendRelationshipNotFoundException exception = new FriendException.FriendRelationshipNotFoundException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND.getMessage());
        assertThat(exception).isInstanceOf(FriendException.class);
    }

    @Test
    @DisplayName("FriendException을 ErrorCode와 커스텀 메시지로 생성할 수 있다")
    void createFriendExceptionWithCustomMessage() {
        // given
        String customMessage = "사용자 A와 사용자 B는 이미 친구입니다.";

        // when
        FriendException exception = new FriendException(ErrorCode.ALREADY_FRIENDS, customMessage);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FRIENDS);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("FriendException을 ErrorCode와 원인 예외로 생성할 수 있다")
    void createFriendExceptionWithCause() {
        // given
        RuntimeException cause = new RuntimeException("Database constraint violation");

        // when
        FriendException exception = new FriendException(ErrorCode.ALREADY_FRIENDS, cause);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ALREADY_FRIENDS);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.ALREADY_FRIENDS.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}