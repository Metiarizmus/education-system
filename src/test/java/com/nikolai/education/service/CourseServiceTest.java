package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.*;
import com.nikolai.education.repository.*;
import com.nikolai.education.util.WorkWithTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;
    @Mock
    private WorkWithTime workWithTime;
    @Mock
    private CacheManagerService<Course> cacheManagerService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private OrgRepo orgRepo;
    @Mock
    private CourseRepo courseRepo;
    @Mock
    private TaskRepo taskRepo;
    @Mock
    private UserLogsRepo userLogsRepo;
    @Mock
    private TaskProgressRepo taskProgressRepo;
    @Mock
    private SendMessages sendMessages;

    private Organization testOrg;
    private User testUser;
    private Course testCourse;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testUser.setId(1L);
        testUser.setEnable(false);

        testCourse = new Course("Course for Java learning", "we are the best", "veryy long plan");
        testCourse.setId(1L);
        testCourse.setCreatorId(2L);

        testTask = new Task("test task", "u must choose correct answer", "its ur first task)");

        testOrg = new Organization("English school", "check kwnolage in English", StatusOrgEnum.PUBLIC);
    }

    @Test
    void createCourse() {
//        when(orgRepo.findByUsers(any(User.class))).thenReturn(testOrg);
//
//        Course result = courseService.createCourse(testCourse, testUser);
//
//        Assertions.assertEquals(testOrg, result.getOrg());
//        Assertions.assertEquals(Collections.singleton(testUser), result.getUsers());
//        Assertions.assertEquals(1L, result.getCreatorId());
//
//        Mockito.verify(courseRepo, Mockito.times(1)).save(testCourse);
    }

    @Test
    void getAllCourses() {
        List<Course> testList = new ArrayList<>();
        testList.add(new Course("Course for Java learning", "we are the best", "veryy long plan"));
        testList.add(new Course("Course for Java learning2", "we are the best2", "veryy long plan2"));
        testList.add(new Course("Course for Java learning3", "we are the best3", "veryy long plan3"));

        when(orgRepo.findByUsers(any(User.class))).thenReturn(testOrg);
        when(courseRepo.findAllByCreatorId(anyLong())).thenReturn(testList);
        when(courseRepo.findAllByUsers(any(User.class))).thenReturn(testList);
        when(courseRepo.findAllByOrg(any(Organization.class))).thenReturn(testList);
        when(cacheManagerService.cachedList(anyString(), anyList())).thenReturn(testList);

        List<Course> result = courseService.getAllCourses(testUser, TypeRolesEnum.ROLE_USER);
        Assertions.assertEquals(3, result.size());

        Assertions.assertEquals(testList.get(0), result.get(0));
    }

    @Test
    void getCourseById() {
        when(courseRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testCourse));
        Course result = courseService.getCourseById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(testCourse.getName(), result.getName());
    }

    @Test
    void getCourseByIdException() {
        assertThatThrownBy(() -> courseService.getCourseById(0L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void startCourse() {
        // TaskProgress taskProgress = new TaskProgress(ProgressTaskEnum.IN_PROGRESS, testTask, testUser);
        List<Task> testList = new ArrayList<>();
        testList.add(new Task("test task", "u must choose correct answer", "its ur first task"));
        testList.add(new Task("test task1", "u must choose correct answer1", "its ur first task2"));
        testList.add(new Task("test task2", "u must choose correct answer2", "its ur first task3"));

        when(courseRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testCourse));
        when(taskRepo.findAllByCourses(testCourse)).thenReturn(testList);
        when(taskRepo.findByCoursesAndNameContains(testCourse, "Test Control")).thenReturn(testTask);

        Task result = courseService.startCourse(1L, testUser);
        Assertions.assertNotNull(result);
        //Assertions.assertEquals();

        Mockito.verify(taskRepo, Mockito.times(1)).save(testTask);
        //Mockito.verify(taskProgressRepo, Mockito.times(1)).save(new TaskProgress(ProgressTaskEnum.IN_PROGRESS, testList.get(0), testUser));
    }

    @Test
    void acceptedCourse() {
        Logs logs = new Logs(UserLogsEnum.ACCEPTED_COURSE, testUser);

        User testRecipient = testUser = new User("Recipient", "Recipient", "recipient@mail.ru",
                "112233", "+37544481965");

        when(userRepo.getById(anyLong())).thenReturn(testUser);
        when(userRepo.findByEmail(anyString())).thenReturn(testRecipient);
        when(courseRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testCourse));

        Course result = courseService.acceptedCourse("email@mail.ru", 1L, 2L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(testCourse.getUsers(), Collections.singleton(testRecipient));

        //Mockito.verify(userLogsRepo, Mockito.times(1)).save(logs);
    }

    @Test
    void deleteCourse() {
        Logs logs = new Logs(UserLogsEnum.DELETE_COURSE, testUser);

        when(courseRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testCourse));
        when(userRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testUser));

        courseService.deleteCourse(1L);
        Mockito.verify(courseRepo, Mockito.times(1)).delete(testCourse);
        //Mockito.verify(userLogsRepo, Mockito.times(1)).save(logs);
    }

}