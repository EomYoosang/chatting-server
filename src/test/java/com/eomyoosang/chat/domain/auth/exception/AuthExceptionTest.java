package com.eomyoosang.chat.domain.auth.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthException 테스트")
class AuthExceptionTest {

    @Test
    @DisplayName("AuthException은 BusinessException을 상속한다")
    void authExceptionExtendsBusinessException() {
        // given & when
        AuthException exception = new AuthException(ErrorCode.INVALID_TOKEN);

        // then
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("InvalidTokenException은 올바른 ErrorCode와 메시지를 가진다")
    void invalidTokenExceptionHasCorrectErrorCode() {
        // given & when
        AuthException.InvalidTokenException exception = new AuthException.InvalidTokenException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_TOKEN.getMessage());
        assertThat(exception).isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("ExpiredTokenException은 올바른 ErrorCode와 메시지를 가진다")
    void expiredTokenExceptionHasCorrectErrorCode() {
        // given & when
        AuthException.ExpiredTokenException exception = new AuthException.ExpiredTokenException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_TOKEN);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.EXPIRED_TOKEN.getMessage());
        assertThat(exception).isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("LoginFailedException은 올바른 ErrorCode와 메시지를 가진다")
    void loginFailedExceptionHasCorrectErrorCode() {
        // given & when
        AuthException.LoginFailedException exception = new AuthException.LoginFailedException();

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.LOGIN_FAILED.getMessage());
        assertThat(exception).isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("AuthException을 ErrorCode와 커스텀 메시지로 생성할 수 있다")
    void createAuthExceptionWithCustomMessage() {
        // given
        String customMessage = "토큰이 만료되었습니다. 다시 로그인해주세요.";

        // when
        AuthException exception = new AuthException(ErrorCode.EXPIRED_TOKEN, customMessage);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EXPIRED_TOKEN);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("AuthException을 ErrorCode와 원인 예외로 생성할 수 있다")
    void createAuthExceptionWithCause() {
        // given
        RuntimeException cause = new RuntimeException("JWT parsing failed");

        // when
        AuthException exception = new AuthException(ErrorCode.INVALID_TOKEN, cause);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
        assertThat(exception.getMessage()).isEqualTo(ErrorCode.INVALID_TOKEN.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}