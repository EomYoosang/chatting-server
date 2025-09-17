package com.eomyoosang.chat.presentation.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterRequest 테스트")
class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("유효한 RegisterRequest는 검증에 통과한다")
    void validRegisterRequestPassesValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "password123",
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("이메일이 null이면 검증에 실패한다")
    void nullEmailFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            null,
            "password123",
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("이메일이 빈 문자열이면 검증에 실패한다")
    void blankEmailFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "",
            "password123",
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 검증에 실패한다")
    void invalidEmailFormatFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "invalid-email",
            "password123",
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    @DisplayName("비밀번호가 null이면 검증에 실패한다")
    void nullPasswordFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            null,
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("비밀번호가 빈 문자열이면 검증에 실패한다")
    void blankPasswordFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "",
            "testuser",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    @DisplayName("닉네임이 null이면 검증에 실패한다")
    void nullNicknameFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "password123",
            null,
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("닉네임이 빈 문자열이면 검증에 실패한다")
    void blankNicknameFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "password123",
            "",
            "010-1234-5678"
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nickname");
    }

    @Test
    @DisplayName("전화번호 형식이 올바르지 않으면 검증에 실패한다")
    void invalidPhoneFormatFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "password123",
            "testuser",
            "010-12345678"  // 잘못된 형식
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        ConstraintViolation<RegisterRequest> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("phone");
        assertThat(violation.getMessage()).isEqualTo("Phone number format should be 010-XXXX-XXXX");
    }

    @Test
    @DisplayName("전화번호가 null이면 검증에 실패한다")
    void nullPhoneFailsValidation() {
        // given
        RegisterRequest request = new RegisterRequest(
            "test@example.com",
            "password123",
            "testuser",
            null
        );

        // when
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("phone");
    }

    @Test
    @DisplayName("올바른 전화번호 형식들이 검증에 통과한다")
    void validPhoneFormatsPassValidation() {
        // given
        String[] validPhones = {
            "010-1234-5678",
            "010-9876-5432",
            "010-0000-0000"
        };

        // when & then
        for (String phone : validPhones) {
            RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "testuser",
                phone
            );

            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("잘못된 전화번호 형식들이 검증에 실패한다")
    void invalidPhoneFormatsFailValidation() {
        // given
        String[] invalidPhones = {
            "01012345678",        // 하이픈 없음
            "010-123-5678",       // 잘못된 자리수
            "010-12345-678",      // 잘못된 자리수
            "011-1234-5678",      // 011로 시작
            "010-1234-567",       // 마지막 자리수 부족
            "010-1234-56789"      // 마지막 자리수 초과
        };

        // when & then
        for (String phone : invalidPhones) {
            RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "testuser",
                phone
            );

            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("phone");
        }
    }

    @Test
    @DisplayName("기본 생성자와 setter로 객체를 생성할 수 있다")
    void createObjectWithDefaultConstructorAndSetters() {
        // given & when
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNickname("testuser");
        request.setPhone("010-1234-5678");

        // then
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getNickname()).isEqualTo("testuser");
        assertThat(request.getPhone()).isEqualTo("010-1234-5678");
    }
}