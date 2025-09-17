package com.eomyoosang.chat.domain.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ErrorCode 테스트")
class ErrorCodeTest {

    @Test
    @DisplayName("공통 에러 코드들의 HTTP 상태, 코드, 메시지가 올바르게 설정되어 있다")
    void commonErrorCodesHaveCorrectValues() {
        // given & when & then
        assertThat(ErrorCode.INVALID_REQUEST.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.INVALID_REQUEST.getCode()).isEqualTo("C001");
        assertThat(ErrorCode.INVALID_REQUEST.getMessage()).isEqualTo("잘못된 요청입니다.");

        assertThat(ErrorCode.METHOD_NOT_ALLOWED.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(ErrorCode.METHOD_NOT_ALLOWED.getCode()).isEqualTo("C002");
        assertThat(ErrorCode.METHOD_NOT_ALLOWED.getMessage()).isEqualTo("지원하지 않는 HTTP 메서드입니다.");

        assertThat(ErrorCode.UNAUTHORIZED.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCode.UNAUTHORIZED.getCode()).isEqualTo("C003");
        assertThat(ErrorCode.UNAUTHORIZED.getMessage()).isEqualTo("인증이 필요합니다.");

        assertThat(ErrorCode.FORBIDDEN.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ErrorCode.FORBIDDEN.getCode()).isEqualTo("C004");
        assertThat(ErrorCode.FORBIDDEN.getMessage()).isEqualTo("접근 권한이 없습니다.");

        assertThat(ErrorCode.NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCode.NOT_FOUND.getCode()).isEqualTo("C005");
        assertThat(ErrorCode.NOT_FOUND.getMessage()).isEqualTo("요청한 리소스를 찾을 수 없습니다.");

        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).isEqualTo("C006");
        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()).isEqualTo("서버 내부 오류가 발생했습니다.");
    }

    @Test
    @DisplayName("사용자 도메인 에러 코드들의 HTTP 상태, 코드, 메시지가 올바르게 설정되어 있다")
    void userDomainErrorCodesHaveCorrectValues() {
        // given & when & then
        assertThat(ErrorCode.USER_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCode.USER_NOT_FOUND.getCode()).isEqualTo("U001");
        assertThat(ErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");

        assertThat(ErrorCode.EMAIL_ALREADY_EXISTS.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorCode.EMAIL_ALREADY_EXISTS.getCode()).isEqualTo("U002");
        assertThat(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");

        assertThat(ErrorCode.NICKNAME_ALREADY_EXISTS.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorCode.NICKNAME_ALREADY_EXISTS.getCode()).isEqualTo("U003");
        assertThat(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage()).isEqualTo("이미 존재하는 닉네임입니다.");

        assertThat(ErrorCode.PHONE_ALREADY_EXISTS.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorCode.PHONE_ALREADY_EXISTS.getCode()).isEqualTo("U004");
        assertThat(ErrorCode.PHONE_ALREADY_EXISTS.getMessage()).isEqualTo("이미 존재하는 전화번호입니다.");

        assertThat(ErrorCode.INVALID_PASSWORD.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.INVALID_PASSWORD.getCode()).isEqualTo("U005");
        assertThat(ErrorCode.INVALID_PASSWORD.getMessage()).isEqualTo("잘못된 비밀번호입니다.");

        assertThat(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND.getCode()).isEqualTo("U006");
        assertThat(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND.getMessage()).isEqualTo("자기 자신을 친구로 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("친구 도메인 에러 코드들의 HTTP 상태, 코드, 메시지가 올바르게 설정되어 있다")
    void friendDomainErrorCodesHaveCorrectValues() {
        // given & when & then
        assertThat(ErrorCode.FRIEND_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCode.FRIEND_NOT_FOUND.getCode()).isEqualTo("F001");
        assertThat(ErrorCode.FRIEND_NOT_FOUND.getMessage()).isEqualTo("친구 관계를 찾을 수 없습니다.");

        assertThat(ErrorCode.ALREADY_FRIENDS.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorCode.ALREADY_FRIENDS.getCode()).isEqualTo("F002");
        assertThat(ErrorCode.ALREADY_FRIENDS.getMessage()).isEqualTo("이미 친구 관계입니다.");

        assertThat(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND.getCode()).isEqualTo("F003");
        assertThat(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND.getMessage()).isEqualTo("친구 관계를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("인증 도메인 에러 코드들의 HTTP 상태, 코드, 메시지가 올바르게 설정되어 있다")
    void authDomainErrorCodesHaveCorrectValues() {
        // given & when & then
        assertThat(ErrorCode.INVALID_TOKEN.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCode.INVALID_TOKEN.getCode()).isEqualTo("A001");
        assertThat(ErrorCode.INVALID_TOKEN.getMessage()).isEqualTo("유효하지 않은 토큰입니다.");

        assertThat(ErrorCode.EXPIRED_TOKEN.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCode.EXPIRED_TOKEN.getCode()).isEqualTo("A002");
        assertThat(ErrorCode.EXPIRED_TOKEN.getMessage()).isEqualTo("만료된 토큰입니다.");

        assertThat(ErrorCode.LOGIN_FAILED.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCode.LOGIN_FAILED.getCode()).isEqualTo("A003");
        assertThat(ErrorCode.LOGIN_FAILED.getMessage()).isEqualTo("로그인에 실패했습니다.");
    }

    @Test
    @DisplayName("검증 에러 코드의 HTTP 상태, 코드, 메시지가 올바르게 설정되어 있다")
    void validationErrorCodeHasCorrectValues() {
        // given & when & then
        assertThat(ErrorCode.VALIDATION_ERROR.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCode.VALIDATION_ERROR.getCode()).isEqualTo("V001");
        assertThat(ErrorCode.VALIDATION_ERROR.getMessage()).isEqualTo("입력값 검증에 실패했습니다.");
    }

    @Test
    @DisplayName("모든 에러 코드는 null이 아닌 값들을 가진다")
    void allErrorCodesHaveNonNullValues() {
        // given & when & then
        for (ErrorCode errorCode : ErrorCode.values()) {
            assertThat(errorCode.getHttpStatus()).isNotNull();
            assertThat(errorCode.getCode()).isNotBlank();
            assertThat(errorCode.getMessage()).isNotBlank();
        }
    }

    @Test
    @DisplayName("에러 코드들은 도메인별로 고유한 코드를 가진다")
    void errorCodesHaveUniqueCodesPerDomain() {
        // given & when & then
        // Common codes (C001-C006)
        assertThat(ErrorCode.INVALID_REQUEST.getCode()).startsWith("C");
        assertThat(ErrorCode.METHOD_NOT_ALLOWED.getCode()).startsWith("C");
        assertThat(ErrorCode.UNAUTHORIZED.getCode()).startsWith("C");
        assertThat(ErrorCode.FORBIDDEN.getCode()).startsWith("C");
        assertThat(ErrorCode.NOT_FOUND.getCode()).startsWith("C");
        assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getCode()).startsWith("C");

        // User codes (U001-U006)
        assertThat(ErrorCode.USER_NOT_FOUND.getCode()).startsWith("U");
        assertThat(ErrorCode.EMAIL_ALREADY_EXISTS.getCode()).startsWith("U");
        assertThat(ErrorCode.NICKNAME_ALREADY_EXISTS.getCode()).startsWith("U");
        assertThat(ErrorCode.PHONE_ALREADY_EXISTS.getCode()).startsWith("U");
        assertThat(ErrorCode.INVALID_PASSWORD.getCode()).startsWith("U");
        assertThat(ErrorCode.CANNOT_ADD_YOURSELF_AS_FRIEND.getCode()).startsWith("U");

        // Friend codes (F001-F003)
        assertThat(ErrorCode.FRIEND_NOT_FOUND.getCode()).startsWith("F");
        assertThat(ErrorCode.ALREADY_FRIENDS.getCode()).startsWith("F");
        assertThat(ErrorCode.FRIEND_RELATIONSHIP_NOT_FOUND.getCode()).startsWith("F");

        // Auth codes (A001-A003)
        assertThat(ErrorCode.INVALID_TOKEN.getCode()).startsWith("A");
        assertThat(ErrorCode.EXPIRED_TOKEN.getCode()).startsWith("A");
        assertThat(ErrorCode.LOGIN_FAILED.getCode()).startsWith("A");

        // Validation codes (V001)
        assertThat(ErrorCode.VALIDATION_ERROR.getCode()).startsWith("V");
    }
}