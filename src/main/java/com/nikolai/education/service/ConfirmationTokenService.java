package com.nikolai.education.service;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.ConfirmationToken;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private final ConfirmTokenRepo linkObjectRepo;
    private final UserService userService;
    private final UserRepo userRepo;

    public void saveConfirmToken(ConfirmationToken confirmationToken, TypeRoles typeRoles) {

        if (!userRepo.existsByEmail(confirmationToken.getUser().getEmail())) {
            userService.saveUser(confirmationToken.getUser(), typeRoles);
        }
        log.info("create confirmation token and save it to db");
        linkObjectRepo.save(confirmationToken);
    }
}
