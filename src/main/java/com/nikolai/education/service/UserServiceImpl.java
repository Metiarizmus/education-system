package com.nikolai.education.service;

import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.UserLogs;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.security.CustomUserDetails;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final PasswordEncoder encoder;
    private final ConvertDto convertDto;
    private final CourseRepo courseRepo;
    private final UserLogsRepo userLogsRepo;
    private final CacheManager<UserDTO> cacheManager;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return CustomUserDetails.build(user);    }

    public void saveUser(User user) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }

        log.info("save user {} to db", user.getEmail());
        userRepo.save(user);
    }

    @Cacheable(value = "Users", key = "#id")
    public UserDTO getById(Long id) {
        return convertDto.convertUser(userRepo.getById(id));
    }

    @Transactional
    public void saveUserInvite(User user, Long senderId, Long courseId) {

        if (user.getPassword() != null) {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        user.setEnable(true);
        Organization org = orgRepo.findByUsers_id(senderId);
        org.getUsers().add(user);

        if (courseId != null) {
            Optional<Course> course = courseRepo.findById(courseId);
            course.get().getUsers().add(user);
            courseRepo.save(course.get());
            log.info("add course to user when he was invited");
        }

    }

    public List<?> getAllUsersInOrg(User user, TypeRoles typeRoles) {
        String key = null;

        switch (typeRoles) {
            case ROLE_USER: key="list:users";break;
            case ROLE_MANAGER: key="list:managers";break;
            case ROLE_ADMIN: key="list:admins";break;
            case ROLE_ROOT_ADMIN: key="list:root-admins";
        }

        Organization organization = orgRepo.findByUsers(user);

            List<User> list = userRepo.findAllByOrgAndRoles_nameRoles(organization, typeRoles);
            if (typeRoles.equals(TypeRoles.ROLE_USER)) {
                list.removeIf(q -> q.getRoles().size() >= 2);
            }
            List<UserDTO> dtos = list.stream().map(convertDto::convertUser).collect(Collectors.toList());

        return cacheManager.cachedList(key,dtos);

    }

    public void deleteUserFromCourse(Long idCourse, Long idUser) {
        Optional<Course> course = courseRepo.findById(idCourse);
        User user = userRepo.getById(idUser);
        course.get().getUsers().remove(user);
        courseRepo.save(course.get());

        Logs logs = new Logs(UserLogs.DELETE_USER_FROM_COURSE, user);
        userLogsRepo.save(logs);

        log.info("delete user {} from course {}", user.getEmail(), course.get().getName());
        System.out.println(course.get().getUsers());
    }

    public void deleteUserFromOrg(Long idUser) {
        Organization org = orgRepo.findByUsers_id(idUser);
        User user = userRepo.getById(idUser);
        org.getUsers().remove(user);
        orgRepo.save(org);

        Logs logs = new Logs(UserLogs.DELETE_USER_FROM_ORG, user);
        userLogsRepo.save(logs);

        log.info("delete user {} from a org {}", user.getEmail(), org.getName());
    }


}