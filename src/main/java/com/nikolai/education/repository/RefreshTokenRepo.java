package com.nikolai.education.repository;

import com.nikolai.education.model.RefreshToken;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    @Override
    Optional<RefreshToken> findById(Long id);
    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
}
