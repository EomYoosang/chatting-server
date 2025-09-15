package com.eomyoosang.chat.application.auth;

import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.domain.user.exception.UserException;
import com.eomyoosang.chat.domain.auth.exception.AuthException;
import com.eomyoosang.chat.infrastructure.security.JwtTokenProvider;
import com.eomyoosang.chat.presentation.auth.dto.AuthResponse;
import com.eomyoosang.chat.presentation.auth.dto.LoginRequest;
import com.eomyoosang.chat.presentation.auth.dto.RegisterRequest;
import com.eomyoosang.chat.presentation.auth.dto.UserInfoResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      AuthenticationManager authenticationManager,
                      JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException.EmailAlreadyExistsException();
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new UserException.PhoneAlreadyExistsException();
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserException.NicknameAlreadyExistsException();
        }

        User user = new User(
                generateULID(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        request.getPassword()
                )
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException.UserNotFoundException());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getId(),
                        request.getPassword()
                )
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new AuthResponse(accessToken, refreshToken);
    }

    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException());

        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                user.getCreatedAt()
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new AuthException.InvalidTokenException();
        }

        String userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException());

        // UserDetails를 직접 생성하고 Authentication 객체 만들기
        org.springframework.security.core.userdetails.UserDetails userDetails =
            org.springframework.security.core.userdetails.User.builder()
                .username(user.getId())
                .password("")  // 토큰 갱신시에는 비밀번호 불필요
                .authorities("USER")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    private String generateULID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 26);
    }
}