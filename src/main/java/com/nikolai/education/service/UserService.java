package com.nikolai.education.service;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final PasswordEncoder encoder;


    public void saveUser(User user, TypeRoles typeRoles) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        user.setRoles(Collections.singleton(new Role(typeRoles)));

        log.info("save user {} to db", user.getEmail());
        userRepo.save(user);

    }

    public void saveUserInvite(User user, TypeRoles typeRoles, Long orgId) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        user.setInvite(true);
        user.setRoles(Collections.singleton(new Role(typeRoles)));

        Organization org = orgRepo.getById(orgId);
        org.getUsers().add(user);

        log.info("save invited user {} to db", user.getEmail());

        userRepo.save(user);
        orgRepo.save(org);
    }

}
