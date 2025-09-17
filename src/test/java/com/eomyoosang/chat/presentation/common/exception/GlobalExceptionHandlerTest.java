package com.eomyoosang.chat.presentation.common.exception;

import com.eomyoosang.chat.domain.auth.exception.AuthException;
import com.eomyoosang.chat.domain.shared.exception.ErrorCode;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.presentation.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("UserException 처리가 올바르게 동작한다")
    void handleUserExceptionCorrectly() {
        // given
        UserException.UserNotFoundException exception = new UserException.UserNotFoundException();

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("AuthException 처리가 올바르게 동작한다")
    void handleAuthExceptionCorrectly() {
        // given
        AuthException.InvalidTokenException exception = new AuthException.InvalidTokenException();

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INVALID_TOKEN.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INVALID_TOKEN.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 처리가 올바르게 동작한다")
    void handleMethodArgumentNotValidExceptionCorrectly() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", "email", "잘못된 이메일 형식입니다.");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("email");
        assertThat(response.getBody().getErrors().get(0).getReason()).isEqualTo("잘못된 이메일 형식입니다.");
    }

    @Test
    @DisplayName("BindException 처리가 올바르게 동작한다")
    void handleBindExceptionCorrectly() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("testObject", "nickname", "닉네임은 필수입니다.");
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        BindException exception = mock(BindException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBindException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getBody().getErrors()).hasSize(1);
        assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException 처리가 올바르게 동작한다")
    void handleMethodArgumentTypeMismatchExceptionCorrectly() {
        // given
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentTypeMismatchException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 처리가 올바르게 동작한다")
    void handleHttpRequestMethodNotSupportedExceptionCorrectly() {
        // given
        HttpRequestMethodNotSupportedException exception =
            new HttpRequestMethodNotSupportedException("POST", java.util.List.of("GET"));

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpRequestMethodNotSupportedException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.METHOD_NOT_ALLOWED.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.METHOD_NOT_ALLOWED.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    @DisplayName("HttpMessageNotReadableException 처리가 올바르게 동작한다")
    void handleHttpMessageNotReadableExceptionCorrectly() {
        // given
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INVALID_REQUEST.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INVALID_REQUEST.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("NoHandlerFoundException 처리가 올바르게 동작한다")
    void handleNoHandlerFoundExceptionCorrectly() throws Exception {
        // given
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/api/nonexistent", null);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNoHandlerFoundException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.NOT_FOUND.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.NOT_FOUND.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("일반 Exception 처리가 올바르게 동작한다")
    void handleGeneralExceptionCorrectly() {
        // given
        Exception exception = new RuntimeException("Unexpected error");

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @DisplayName("모든 ErrorResponse에는 타임스탬프가 포함된다")
    void allErrorResponsesIncludeTimestamp() {
        // given
        UserException.UserNotFoundException exception = new UserException.UserNotFoundException();

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception);

        // then
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("BusinessException의 하위 클래스들이 올바른 HTTP 상태 코드를 반환한다")
    void businessExceptionSubclassesReturnCorrectStatusCodes() {
        // given & when & then
        // CONFLICT 상태 테스트
        UserException.EmailAlreadyExistsException emailException = new UserException.EmailAlreadyExistsException();
        ResponseEntity<ErrorResponse> emailResponse = exceptionHandler.handleBusinessException(emailException);
        assertThat(emailResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // UNAUTHORIZED 상태 테스트
        AuthException.ExpiredTokenException tokenException = new AuthException.ExpiredTokenException();
        ResponseEntity<ErrorResponse> tokenResponse = exceptionHandler.handleBusinessException(tokenException);
        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // BAD_REQUEST 상태 테스트
        UserException.CannotAddYourselfAsFriendException friendException = new UserException.CannotAddYourselfAsFriendException();
        ResponseEntity<ErrorResponse> friendResponse = exceptionHandler.handleBusinessException(friendException);
        assertThat(friendResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("여러 필드 검증 오류가 모두 포함된다")
    void multipleFieldErrorsAreIncluded() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError emailError = new FieldError("testObject", "email", "이메일은 필수입니다.");
        FieldError passwordError = new FieldError("testObject", "password", "비밀번호는 필수입니다.");
        FieldError nicknameError = new FieldError("testObject", "nickname", "닉네임은 필수입니다.");

        when(bindingResult.getFieldErrors()).thenReturn(
            java.util.List.of(emailError, passwordError, nicknameError)
        );

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        // when
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(exception);

        // then
        assertThat(response.getBody().getErrors()).hasSize(3);
        assertThat(response.getBody().getErrors())
            .extracting("field")
            .containsExactly("email", "password", "nickname");
        assertThat(response.getBody().getErrors())
            .extracting("reason")
            .containsExactly("이메일은 필수입니다.", "비밀번호는 필수입니다.", "닉네임은 필수입니다.");
    }
}