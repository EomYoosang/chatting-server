package com.eomyoosang.chat.presentation.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginRequest 테스트")
class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("유효한 LoginRequest는 검증에 통과한다")
    void validLoginRequestPassesValidation() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이메일이 null이면 검증에 실패한다")
    void nullEmailFailsValidation() {
        // given
        LoginRequest request = new LoginRequest(null, "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 검증에 실패한다")
    void blankEmailFailsValidation() {
        // given
        LoginRequest request = new LoginRequest("", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 검증에 실패한다")
    void invalidEmailFormatFailsValidation() {
        // given
        LoginRequest request = new LoginRequest("invalid-email", "password123");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("비밀번호가 null이면 검증에 실패한다")
    void nullPasswordFailsValidation() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", null);

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열이면 검증에 실패한다")
    void blankPasswordFailsValidation() {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("이메일과 비밀번호가 모두 유효하지 않으면 두 개의 검증 오류가 발생한다")
    void bothInvalidEmailAndPasswordFailValidation() {
        // given
        LoginRequest request = new LoginRequest("", "");

        // when
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSizeGreaterThanOrEqualTo(2);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    @DisplayName("기본 생성자와 setter로 객체를 생성할 수 있다")
    void createObjectWithDefaultConstructorAndSetters() {
        // given & when
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // then
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("유효한 이메일 형식들이 검증에 통과한다")
    void validEmailFormatsPassValidation() {
        // given
        String[] validEmails = {
            "test@example.com",
            "user123@domain.co.kr",
            "first.last@subdomain.example.org",
            "user+tag@example.com"
        };

        // when & then
        for (String email : validEmails) {
            LoginRequest request = new LoginRequest(email, "password123");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("잘못된 이메일 형식들이 검증에 실패한다")
    void invalidEmailFormatsFailValidation() {
        // given
        String[] invalidEmails = {
            "plainaddress",
            "@missingdomain.com",
            "missing@.com",
            "spaces @domain.com",
            "double@@domain.com"
        };

        // when & then
        for (String email : invalidEmails) {
            LoginRequest request = new LoginRequest(email, "password123");
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
        }
    }
}