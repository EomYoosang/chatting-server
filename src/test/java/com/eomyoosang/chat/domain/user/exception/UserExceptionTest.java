package com.eomyoosang.chat.domain.user.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserException 테스트")
class UserExceptionTest {

    @Test
    @DisplayName("UserException은 BusinessException을 상속한다")
    void userExceptionExtendsBusinessException() {
        // given & when
        UserException exception = new UserException(ErrorCode.USER_NOT_FOUND);

        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("UserNotFoundException은 올바른 ErrorCode와 메시지를 가진다")
    void userNotFoundExceptionHasCorrectErrorCode() {
        // given & when
        UserException.UserNotFoundException exception = new UserException.UserNotFoundException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        assertThat(exception).isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("EmailAlreadyExistsException은 올바른 ErrorCode와 메시지를 가진다")
    void emailAlreadyExistsExceptionHasCorrectErrorCode() {
        // given & when
        UserException.EmailAlreadyExistsException exception = new UserException.EmailAlreadyExistsException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        assertThat(exception).isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("NicknameAlreadyExistsException은 올바른 ErrorCode와 메시지를 가진다")
    void nicknameAlreadyExistsExceptionHasCorrectErrorCode() {
        // given & when
        UserException.NicknameAlreadyExistsException exception = new UserException.NicknameAlreadyExistsException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
        assertThat(exception).isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("PhoneAlreadyExistsException은 올바른 ErrorCode와 메시지를 가진다")
    void phoneAlreadyExistsExceptionHasCorrectErrorCode() {
        // given & when
        UserException.PhoneAlreadyExistsException exception = new UserException.PhoneAlreadyExistsException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PHONE_ALREADY_EXISTS);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.PHONE_ALREADY_EXISTS.getMessage());
        assertThat(exception).isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("CannotAddYourselfAsFriendException은 올바른 ErrorCode와 메시지를 가진다")
    void cannotAddYourselfAsFriendExceptionHasCorrectErrorCode() {
        // given & when
        UserException.CannotAddYourselfAsFriendException exception = new UserException.CannotAddYourselfAsFriendException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND.getMessage());
        assertThat(exception).isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("UserException을 ErrorCode와 커스텀 메시지로 생성할 수 있다")
    void createUserExceptionWithCustomMessage() {
        // given
        String customMessage = "사용자 ID: user123을 찾을 수 없습니다.";

        // when
        UserException exception = new UserException(ErrorCode.USER_NOT_FOUND, customMessage);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("UserException을 ErrorCode와 원인 예외로 생성할 수 있다")
    void createUserExceptionWithCause() {
        // given
        RuntimeException cause = new RuntimeException("Database connection failed");

        // when
        UserException exception = new UserException(ErrorCode.USER_NOT_FOUND, cause);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}