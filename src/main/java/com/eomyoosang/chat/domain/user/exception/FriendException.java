package com.eomyoosang.chat.domain.user.exception;

import com.eomyoosang.chat.domain.shared.exception.BusinessException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;

public class FriendException extends BusinessException {

    public FriendException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FriendException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public FriendException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public static class FriendNotFoundException extends FriendException {
        public FriendNotFoundException() {
            super(ErrorCode.FRIEND_NOT_FOUND);
        }
    }

    public static class AlreadyFriendsException extends FriendException {
        public AlreadyFriendsException() {
            super(ErrorCode.ALREADY_FRIENDS);
        }
    }

    public static class FriendRelationshipNotFoundException extends FriendException {
        public FriendRelationshipNotFoundException() {
            super(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND);
        }
    }
}