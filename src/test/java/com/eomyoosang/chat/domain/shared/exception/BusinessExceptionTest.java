package com.eomyoosang.chat.domain.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BusinessException 테스트")
class BusinessExceptionTest {

    @Test
    @DisplayName("ErrorCode만으로 예외를 생성하면 ErrorCode의 메시지를 사용한다")
    void createExceptionWithErrorCodeOnly() {
        // given
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        // when
        TestBusinessException exception = new TestBusinessException(errorCode);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("ErrorCode와 커스텀 메시지로 예외를 생성하면 커스텀 메시지를 사용한다")
    void createExceptionWithCustomMessage() {
        // given
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        String customMessage = "사용자 ID: 123을 찾을 수 없습니다.";

        // when
        TestBusinessException exception = new TestBusinessException(errorCode, customMessage);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("ErrorCode와 cause로 예외를 생성하면 ErrorCode의 메시지와 원인을 포함한다")
    void createExceptionWithCause() {
        // given
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        RuntimeException cause = new RuntimeException("원인 예외");

        // when
        TestBusinessException exception = new TestBusinessException(errorCode, cause);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("BusinessException은 RuntimeException을 상속한다")
    void businessExceptionExtendsRuntimeException() {
        // given
        TestBusinessException exception = new TestBusinessException(ErrorCode.USER_NOT_FOUND);

        // when & then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    private static class TestBusinessException extends BusinessException {
        public TestBusinessException(ErrorCode errorCode) {
            super(errorCode);
        }

        public TestBusinessException(ErrorCode errorCode, String message) {
            super(errorCode, message);
        }

        public TestBusinessException(ErrorCode errorCode, Throwable cause) {
            super(errorCode, cause);
        }
    }
}