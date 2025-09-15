package com.eomyoosang.chat.application.user;

import com.eomyoosang.chat.domain.user.entity.User;
import com.eomyoosang.chat.domain.user.repository.UserRepository;
import com.eomyoosang.chat.presentation.user.dto.UserProfileDto;
import com.eomyoosang.chat.presentation.user.dto.UpdateProfileRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileDto.fromEntity(user);
    }

    public UserProfileDto getMyProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileDto.fromEntityWithPrivateInfo(user);
    }

    @Transactional
    public UserProfileDto updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 닉네임 중복 검증
        if (request.getNickname() != null &&
            !request.getNickname().equals(user.getNickname()) &&
            userRepository.existsByNickname(request.getNickname())) {
            throw new RuntimeException("Nickname already exists");
        }

        // 프로필 업데이트
        user.updateProfile(
            request.getNickname(),
            request.getStatusMessage(),
            request.getProfileImageUrl()
        );

        User savedUser = userRepository.save(user);
        return UserProfileDto.fromEntityWithPrivateInfo(savedUser);
    }

    public Page<UserProfileDto> searchUsers(String query, String currentUserId, Pageable pageable) {
        // 이메일 또는 닉네임으로 검색
        List<User> users;
        if (query.contains("@")) {
            // 이메일로 검색
            users = userRepository.findByEmail(query)
                    .map(List::of)
                    .orElse(List.of());
        } else {
            // 닉네임으로 검색
            users = userRepository.findByNicknameContaining(query);
        }

        // 본인 제외
        users = users.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(User::getIsActive)
                .collect(Collectors.toList());

        List<UserProfileDto> userDtos = users.stream()
                .map(UserProfileDto::fromEntity)
                .collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userDtos.size());

        if (start >= userDtos.size()) {
            return new PageImpl<>(List.of(), pageable, userDtos.size());
        }

        return new PageImpl<>(
            userDtos.subList(start, end),
            pageable,
            userDtos.size()
        );
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}