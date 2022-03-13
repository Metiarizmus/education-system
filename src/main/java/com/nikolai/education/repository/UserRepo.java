package com.nikolai.education.repository;

import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByEmail(String email);
}
