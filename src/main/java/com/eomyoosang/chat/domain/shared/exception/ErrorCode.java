package com.eomyoosang.chat.domain.shared.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common Errors
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C002", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 내부 오류가 발생했습니다."),

    // User Domain Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U003", "이미 존재하는 닉네임입니다."),
    PHONE_ALREADY_EXISTS(HttpStatus.CONFLICT, "U004", "이미 존재하는 전화번호입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U005", "잘못된 비밀번호입니다."),
    CANNOT_ADD_YOURSELF_AS_FRIEND(HttpStatus.BAD_REQUEST, "U006", "자기 자신을 친구로 추가할 수 없습니다."),

    // Friend Domain Errors
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "친구 관계를 찾을 수 없습니다."),
    ALREADY_FRIENDS(HttpStatus.CONFLICT, "F002", "이미 친구 관계입니다."),
    FRIEND_RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "F003", "친구 관계를 찾을 수 없습니다."),

    // Auth Domain Errors
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A003", "로그인에 실패했습니다."),

    // Validation Errors
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "V001", "입력값 검증에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}