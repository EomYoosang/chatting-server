package com.eomyoosang.chat.domain.user.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;

public class UserException extends BusinessException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException() {
            super(ErrorCode.USER_NOT_FOUND);
        }
    }

    public static class EmailAlreadyExistsException extends UserException {
        public EmailAlreadyExistsException() {
            super(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    public static class NicknameAlreadyExistsException extends UserException {
        public NicknameAlreadyExistsException() {
            super(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    public static class PhoneAlreadyExistsException extends UserException {
        public PhoneAlreadyExistsException() {
            super(ErrorCode.PHONE_ALREADY_EXISTS);
        }
    }

    public static class CannotAddYourselfAsFriendException extends UserException {
        public CannotAddYourselfAsFriendException() {
            super(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND);
        }
    }
}