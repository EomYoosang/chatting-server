package com.eomyoosang.chat.presentation.common.dto;

import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ErrorResponse 테스트")
class ErrorResponseTest {

    @Test
    @DisplayName("ErrorCode만으로 ErrorResponse를 생성할 수 있다")
    void createErrorResponseWithErrorCodeOnly() {
        // given
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode);

        // then
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(response.getStatus()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(response.getErrors()).isEmpty();
        assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("ErrorCode와 커스텀 메시지로 ErrorResponse를 생성할 수 있다")
    void createErrorResponseWithCustomMessage() {
        // given
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        String customMessage = "사용자 ID: user123을 찾을 수 없습니다.";

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, customMessage);

        // then
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(customMessage);
        assertThat(response.getStatus()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(response.getErrors()).isEmpty();
        assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("ErrorCode와 BindingResult로 ErrorResponse를 생성할 수 있다")
    void createErrorResponseWithBindingResult() {
        // given
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("registerRequest", "email", "이메일 형식이 올바르지 않습니다.");
        FieldError fieldError2 = new FieldError("registerRequest", "password", "비밀번호는 필수입니다.");

        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, bindingResult);

        // then
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(response.getStatus()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(response.getErrors()).hasSize(2);

        ErrorResponse.FieldErrorDetail error1 = response.getErrors().get(0);
        assertThat(error1.getField()).isEqualTo("email");
        assertThat(error1.getReason()).isEqualTo("이메일 형식이 올바르지 않습니다.");

        ErrorResponse.FieldErrorDetail error2 = response.getErrors().get(1);
        assertThat(error2.getField()).isEqualTo("password");
        assertThat(error2.getReason()).isEqualTo("비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("ErrorCode와 FieldErrorDetail 리스트로 ErrorResponse를 생성할 수 있다")
    void createErrorResponseWithFieldErrorDetails() {
        // given
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        List<ErrorResponse.FieldErrorDetail> errors = Arrays.asList(
            createFieldErrorDetail("email", "invalid-email", "이메일 형식이 올바르지 않습니다."),
            createFieldErrorDetail("phone", "123", "전화번호 형식이 올바르지 않습니다.")
        );

        // when
        ErrorResponse response = ErrorResponse.of(errorCode, errors);

        // then
        assertThat(response.getCode()).isEqualTo(errorCode.getCode());
        assertThat(response.getMessage()).isEqualTo(errorCode.getMessage());
        assertThat(response.getStatus()).isEqualTo(errorCode.getHttpStatus().value());
        assertThat(response.getErrors()).hasSize(2);
        assertThat(response.getErrors()).containsExactlyElementsOf(errors);
    }

    @Test
    @DisplayName("타임스탬프는 현재 시간 이전이거나 같다")
    void timestampIsBeforeOrEqualToCurrentTime() {
        // given
        LocalDateTime before = LocalDateTime.now();
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        // when
        ErrorResponse response = ErrorResponse.of(errorCode);
        LocalDateTime after = LocalDateTime.now();

        // then
        assertThat(response.getTimestamp()).isBetween(before, after);
    }

    @Test
    @DisplayName("FieldErrorDetail은 BindingResult로부터 올바르게 생성된다")
    void fieldErrorDetailFromBindingResult() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userDto", "nickname", "닉네임은 3자 이상이어야 합니다.");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // when
        List<ErrorResponse.FieldErrorDetail> details = ErrorResponse.FieldErrorDetail.of(bindingResult);

        // then
        assertThat(details).hasSize(1);
        ErrorResponse.FieldErrorDetail detail = details.get(0);
        assertThat(detail.getField()).isEqualTo("nickname");
        assertThat(detail.getReason()).isEqualTo("닉네임은 3자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("FieldError의 rejectedValue가 null인 경우 빈 문자열로 처리된다")
    void fieldErrorWithNullRejectedValue() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userDto", "email", "이메일은 필수입니다.");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // when
        List<ErrorResponse.FieldErrorDetail> details = ErrorResponse.FieldErrorDetail.of(bindingResult);

        // then
        assertThat(details).hasSize(1);
        ErrorResponse.FieldErrorDetail detail = details.get(0);
        assertThat(detail.getField()).isEqualTo("email");
        assertThat(detail.getReason()).isEqualTo("이메일은 필수입니다.");
    }

    private ErrorResponse.FieldErrorDetail createFieldErrorDetail(String field, String value, String reason) {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", field, reason);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        return ErrorResponse.FieldErrorDetail.of(bindingResult).get(0);
    }
}