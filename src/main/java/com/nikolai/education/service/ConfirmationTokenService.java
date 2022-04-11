package com.nikolai.education.service;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.Role;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private final ConfirmTokenRepo linkObjectRepo;
    private final UserServiceImpl userService;
    private final UserRepo userRepo;

    public void saveConfirmToken(InvitationLink confirmationToken, TypeRoles typeRoles) {

        if (!userRepo.existsByEmail(confirmationToken.getUser().getEmail())) {
            confirmationToken.getUser().setRoles(Collections.singleton(new Role(typeRoles)));
            userService.saveUser(confirmationToken.getUser());
        }
        log.info("create confirmation token and save it to db");
        linkObjectRepo.save(confirmationToken);
    }
}
