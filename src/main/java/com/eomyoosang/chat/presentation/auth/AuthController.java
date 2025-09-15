package com.eomyoosang.chat.presentation.auth;

import com.eomyoosang.chat.application.auth.AuthService;
import com.eomyoosang.chat.infrastructure.security.UserPrincipal;
import com.eomyoosang.chat.presentation.auth.dto.AuthResponse;
import com.eomyoosang.chat.presentation.auth.dto.LoginRequest;
import com.eomyoosang.chat.presentation.auth.dto.RegisterRequest;
import com.eomyoosang.chat.presentation.auth.dto.UserInfoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInfoResponse response = authService.getUserInfo(userPrincipal.getId());
        return ResponseEntity.ok(response);
    }
}