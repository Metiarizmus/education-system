package com.nikolai.education.repository;

import com.nikolai.education.model.InvitationLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmTokenRepo extends JpaRepository<InvitationLink, Long> {

    InvitationLink findByConfirmationToken(String token);

}
