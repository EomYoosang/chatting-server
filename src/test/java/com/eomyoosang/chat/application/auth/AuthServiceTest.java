package com.eomyoosang.chat.application.auth;

import com.eomyoosang.chat.domain.auth.exception.AuthException;
import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.infrastructure.security.JwtTokenProvider;
import com.eomyoosang.chat.presentation.auth.dto.AuthResponse;
import com.eomyoosang.chat.presentation.auth.dto.LoginRequest;
import com.eomyoosang.chat.presentation.auth.dto.RegisterRequest;
import com.eomyoosang.chat.presentation.auth.dto.UserInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@DisplayName("AuthService 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
            "test@example.com",
            "password123",
            "testuser",
            "010-1234-5678"
        );

        loginRequest = new LoginRequest("test@example.com", "password123");

        user = new User(
            "user123",
            "test@example.com",
            "010-1234-5678",
            "encodedPassword",
            "testuser"
        );
    }

    @Test
    @DisplayName("새로운 사용자 등록이 성공한다")
    void registerNewUserSuccess() {
        // given
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(registerRequest.getPhone())).willReturn(false);
        given(userRepository.existsByNickname(registerRequest.getNickname())).willReturn(false);
        given(passwordEncoder.encode(registerRequest.getPassword())).willReturn("encodedPassword");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);
        given(tokenProvider.generateAccessToken(authentication)).willReturn("accessToken");
        given(tokenProvider.generateRefreshToken(authentication)).willReturn("refreshToken");

        // when
        AuthResponse response = authService.register(registerRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(registerRequest.getEmail());
        assertThat(savedUser.getNickname()).isEqualTo(registerRequest.getNickname());
        assertThat(savedUser.getPhone()).isEqualTo(registerRequest.getPhone());
        assertThat(savedUser.getPasswordHash()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 등록 시 예외가 발생한다")
    void registerWithExistingEmailThrowsException() {
        // given
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(UserException.EmailAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 전화번호로 등록 시 예외가 발생한다")
    void registerWithExistingPhoneThrowsException() {
        // given
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(registerRequest.getPhone())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(UserException.PhoneAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 닉네임으로 등록 시 예외가 발생한다")
    void registerWithExistingNicknameThrowsException() {
        // given
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(registerRequest.getPhone())).willReturn(false);
        given(userRepository.existsByNickname(registerRequest.getNickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(UserException.NicknameAlreadyExistsException.class);

        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("유효한 사용자 정보로 로그인이 성공한다")
    void loginWithValidCredentialsSuccess() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);
        given(tokenProvider.generateAccessToken(authentication)).willReturn("accessToken");
        given(tokenProvider.generateRefreshToken(authentication)).willReturn("refreshToken");

        // when
        AuthResponse response = authService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
            ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        then(authenticationManager).should().authenticate(authCaptor.capture());

        UsernamePasswordAuthenticationToken authToken = authCaptor.getValue();
        assertThat(authToken.getPrincipal()).isEqualTo(user.getId());
        assertThat(authToken.getCredentials()).isEqualTo(loginRequest.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외가 발생한다")
    void loginWithNonExistentEmailThrowsException() {
        // given
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(UserException.UserNotFoundException.class);

        then(authenticationManager).should(never()).authenticate(any());
    }

    @Test
    @DisplayName("사용자 정보 조회가 성공한다")
    void getUserInfoSuccess() {
        // given
        String userId = "user123";
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        UserInfoResponse response = authService.getUserInfo(userId);

        // then
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getNickname()).isEqualTo(user.getNickname());
        assertThat(response.getPhone()).isEqualTo(user.getPhone());
        assertThat(response.getCreatedAt()).isEqualTo(user.getCreatedAt());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보 조회 시 예외가 발생한다")
    void getUserInfoWithNonExistentUserThrowsException() {
        // given
        String userId = "nonexistent";
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.getUserInfo(userId))
            .isInstanceOf(UserException.UserNotFoundException.class);
    }

    @Test
    @DisplayName("유효한 리프레시 토큰으로 토큰 갱신이 성공한다")
    void refreshTokenWithValidTokenSuccess() {
        // given
        String refreshToken = "validRefreshToken";
        String userId = "user123";

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(tokenProvider.generateAccessToken(any(Authentication.class))).willReturn("newAccessToken");
        given(tokenProvider.generateRefreshToken(any(Authentication.class))).willReturn("newRefreshToken");

        // when
        AuthResponse response = authService.refreshToken(refreshToken);

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 토큰 갱신 시 예외가 발생한다")
    void refreshTokenWithInvalidTokenThrowsException() {
        // given
        String invalidRefreshToken = "invalidRefreshToken";
        given(tokenProvider.validateToken(invalidRefreshToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
            .isInstanceOf(AuthException.InvalidTokenException.class);

        then(tokenProvider).should(never()).getUserIdFromToken(anyString());
        then(userRepository).should(never()).findById(anyString());
    }

    @Test
    @DisplayName("리프레시 토큰의 사용자가 존재하지 않으면 예외가 발생한다")
    void refreshTokenWithNonExistentUserThrowsException() {
        // given
        String refreshToken = "validRefreshToken";
        String userId = "nonexistentUser";

        given(tokenProvider.validateToken(refreshToken)).willReturn(true);
        given(tokenProvider.getUserIdFromToken(refreshToken)).willReturn(userId);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
            .isInstanceOf(UserException.UserNotFoundException.class);

        then(tokenProvider).should(never()).generateAccessToken(any());
        then(tokenProvider).should(never()).generateRefreshToken(any());
    }

    @Test
    @DisplayName("등록 시 생성되는 사용자 ID는 26자리 문자열이다")
    void registerGeneratesValidUserId() {
        // given
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(false);
        given(userRepository.existsByPhone(registerRequest.getPhone())).willReturn(false);
        given(userRepository.existsByNickname(registerRequest.getNickname())).willReturn(false);
        given(passwordEncoder.encode(registerRequest.getPassword())).willReturn("encodedPassword");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .willReturn(authentication);
        given(tokenProvider.generateAccessToken(authentication)).willReturn("accessToken");
        given(tokenProvider.generateRefreshToken(authentication)).willReturn("refreshToken");

        // when
        authService.register(registerRequest);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        then(userRepository).should().save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getId()).hasSize(26);
        assertThat(savedUser.getId()).matches("^[a-zA-Z0-9]+$");
    }
}