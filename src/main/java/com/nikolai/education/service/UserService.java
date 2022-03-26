package com.nikolai.education.service;

import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final PasswordEncoder encoder;
    private final ConvertDto convertDto;
    private final CourseRepo courseRepo;

    public void saveUser(User user, TypeRoles typeRoles) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        user.setRoles(Collections.singleton(new Role(typeRoles)));

        log.info("save user {} to db", user.getEmail());
        userRepo.save(user);

    }

    @Transactional
    public void saveUserInvite(User user, Long senderId, Long courseId) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        //if (typeWayInvited.equals(TypeWayInvited.MAIL)) {
            user.setEnable(true);
       // }

        if (courseId != null) {
            Optional<Course> course = courseRepo.findById(courseId);
            user.setCourses(Collections.singleton(course.get()));
            log.info("add course to user when he was invited");
        }

        Organization org = orgRepo.findByUsers_id(senderId);
        org.getUsers().add(user);

        log.info("save invited user {} to db", user.getEmail());

        userRepo.save(user);
        orgRepo.save(org);
    }

    public List<UserDTO> getAllUsersInOrg(Principal principal, TypeRoles typeRoles) {
        User user = userRepo.findByEmail(principal.getName());
        Organization organization = orgRepo.findByUsers(user);

        List<User> list = userRepo.findAllByOrgAndRoles_nameRoles(organization, typeRoles);

        if (typeRoles.equals(TypeRoles.ROLE_USER)) {
            list.removeIf(q -> q.getRoles().size() >= 2);
        }

        return list.stream().map(p -> convertDto.convertUser(p)).collect(Collectors.toList());
    }


}
