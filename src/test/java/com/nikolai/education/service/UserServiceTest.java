package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.redis.RedisService;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableCaching
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private CacheManagerService<User> cacheManagerService;
    @Mock
    private RedisService redisService;

    @Mock
    private UserRepo userRepo;
    @Mock
    private OrgRepo orgRepo;
    @Mock
    private CourseRepo courseRepo;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserLogsRepo userLogsRepo;

    private Organization testOrg;
    private User testUser;
    private Course testCourse;

    @BeforeEach
    public void setUp() {
        testOrg = new Organization("English school", "check kwnolage in English", StatusOrgEnum.PUBLIC);
        testOrg.setId(10L);

        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testUser.setEnable(false);

        testCourse = new Course("Course for Java learning", "we are the best", "veryy long plan");
    }

    @Test
    void saveUser() {
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn(testUser.getPassword());
        userService.saveUser(testUser);
        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(testUser.getPassword());
        Mockito.verify(userRepo, Mockito.times(1)).save(testUser);
    }

    @Test
    void getById() {
        when(userRepo.findById(any(Long.class))).thenReturn(Optional.of(testUser));

        User user = userService.getById(1L);
        Assertions.assertEquals("Nikolai", user.getFirstName());
        Assertions.assertEquals("stormytt@mail.ru", user.getEmail());
        Mockito.verify(userRepo, Mockito.times(1)).findById(1L);
    }

    @Test
    void saveUserInvite() {

        when(passwordEncoder.encode(testUser.getPassword())).thenReturn(testUser.getPassword());
        when(orgRepo.findByUsers_id(any(Long.class))).thenReturn(testOrg);
        when(courseRepo.findById(any(Long.class))).thenReturn(Optional.ofNullable(testCourse));
        userService.saveUserInvite(testUser, 1L, 1L);

        Assertions.assertEquals(testUser.isEnable(), true);
        Assertions.assertEquals(testOrg.getUsers(), Collections.singleton(testUser));
        Assertions.assertEquals(testCourse.getUsers(), Collections.singleton(testUser));

        Mockito.verify(passwordEncoder, Mockito.times(1)).encode(testUser.getPassword());
        Mockito.verify(orgRepo, Mockito.times(1)).findByUsers_id(any(Long.class));
        Mockito.verify(courseRepo, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    void getAllUsersInOrg() {
        List<User> testList = new ArrayList<>();
        testList.add(new User("first", "last", "first@mail.ru", "112233", "5469888"));
        testList.add(new User("first1", "last1", "first1@mail.ru", "112233", "546955888"));
        testList.add(new User("first2", "last2", "first2@mail.ru", "112233", "5478969888"));

        when(orgRepo.findByUsers(any(User.class))).thenReturn(testOrg);
        when(cacheManagerService.cachedList(anyString(), testList)).thenReturn(testList);

        when(userService.getAllUsersInOrg(testUser, TypeRolesEnum.ROLE_USER)).thenReturn(testList);
        List<User> result = userService.getAllUsersInOrg(testUser, TypeRolesEnum.ROLE_USER);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    void deleteUserFromCourse() {

        testCourse.setUsers(Collections.singleton(testUser));
        testCourse.setUsers(Collections.singleton(new User("q", "b", "c", "a", "s")));

        when(courseRepo.findById(any(Long.class))).thenReturn(Optional.ofNullable(testCourse));
        when(userRepo.getById(any(Long.class))).thenReturn(testUser);

        userService.deleteUserFromCourse(1L, 1L);

        Mockito.verify(userRepo, Mockito.times(1)).getById(any(Long.class));
        Mockito.verify(courseRepo, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    void deleteUserFromOrg() {
        testOrg.setUsers(Collections.singleton(testUser));
        testOrg.setUsers(Collections.singleton(new User("q", "b", "c", "a", "s")));

        when(orgRepo.findByUsers_id(any(Long.class))).thenReturn(testOrg);
        when(userRepo.getById(any(Long.class))).thenReturn(testUser);

        userService.deleteUserFromOrg(1L);

        Mockito.verify(userRepo, Mockito.times(1)).getById(any(Long.class));
        Mockito.verify(orgRepo, Mockito.times(1)).findByUsers_id(any(Long.class));
    }


}
