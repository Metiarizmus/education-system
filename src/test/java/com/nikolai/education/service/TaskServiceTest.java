package com.nikolai.education.service;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.TaskProgress;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.TaskProgressRepo;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private CourseRepo courseRepo;
    @Mock
    private UserLogsRepo userLogsRepo;
    @Mock
    private TaskProgressRepo taskProgressRepo;

    private User testUser;
    private Course testCourse;
    private Task testTask;
    private TaskProgress taskProgress;

    @Rule
    private ExpectedException expectedException = ExpectedException.none();


    @BeforeEach
    void setUp() {
        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testUser.setEnable(false);

        testCourse = new Course("Course for Java learning", "we are the best", "veryy long plan");

        testTask = new Task("test task", "u must choose correct answer", "its ur first task)");

        taskProgress = new TaskProgress(ProgressTaskEnum.DONE, testTask, testUser);
    }

    @Test
    void createTask() {

        when(courseRepo.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(testCourse));
        when(userRepo.getById(anyLong())).thenReturn(testUser);

        Task result = taskService.createTask(testTask, 1L, 72);
        Assertions.assertEquals(testCourse.getTasks(), Collections.singleton(result));
        Assertions.assertEquals(result.getExpirationCountHours(), 72);
    }

    @Test
    void createTaskException() {
        assertThatThrownBy(() -> taskService.createTask(testTask, null, 11)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void changeStatusTask() {
        when(taskProgressRepo.getByTaskId(anyLong())).thenReturn(taskProgress);
        taskService.changeStatusTask(1L, ProgressTaskEnum.DONE);

        Mockito.verify(taskProgressRepo, Mockito.times(1)).save(taskProgress);
    }

    @Test
    void changeStatusTaskException() {
        assertThatThrownBy(() -> taskService.changeStatusTask(null, ProgressTaskEnum.DONE)).isInstanceOf(ResourceNotFoundException.class);
    }

}