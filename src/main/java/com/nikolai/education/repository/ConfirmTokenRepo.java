package com.nikolai.education.repository;

import com.nikolai.education.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmTokenRepo extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByConfirmationToken(String token);

}
