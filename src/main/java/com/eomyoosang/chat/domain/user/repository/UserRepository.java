package com.eomyoosang.chat.domain.user.repository;

import com.eomyoosang.chat.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    List<User> findByNicknameContaining(String nickname);
    List<User> findAll();
    void deleteById(String id);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByNickname(String nickname);
}