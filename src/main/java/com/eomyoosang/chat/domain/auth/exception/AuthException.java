package com.eomyoosang.chat.domain.auth.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public static class InvalidTokenException extends AuthException {
        public InvalidTokenException() {
            super(ErrorCode.INVALID_TOKEN);
        }
    }

    public static class ExpiredTokenException extends AuthException {
        public ExpiredTokenException() {
            super(ErrorCode.EXPIRED_TOKEN);
        }
    }

    public static class LoginFailedException extends AuthException {
        public LoginFailedException() {
            super(ErrorCode.LOGIN_FAILED);
        }
    }
}