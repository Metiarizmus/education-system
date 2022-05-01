package com.nikolai.education.util;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ConvertDtoTest {

    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        this.mapper = new ModelMapper();
    }

    @Test
    void convertOrg() {
        Organization testOrg = new Organization("English school", "check kwnolage in English", StatusOrgEnum.PUBLIC);
        testOrg.setId(10L);

        OrgDTO result = mapper.map(testOrg, OrgDTO.class);
        Assertions.assertEquals(testOrg.getName(), result.getName());
        Assertions.assertEquals(testOrg.getDescription(), result.getDescription());
        Assertions.assertEquals(testOrg.getStatus(), result.getStatus());
        Assertions.assertEquals(testOrg.getId(), result.getId());
    }

    @Test
    void convertUser() {
        User testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");

        UserDTO result = mapper.map(testUser, UserDTO.class);
        Assertions.assertEquals(testUser.getFirstName(), result.getFirstName());
        Assertions.assertEquals(testUser.getPassword(), result.getPassword());
        Assertions.assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void convertCourse() {
        Course testCourse = new Course("Course for Java learning", "we are the best", "veryy long plan");

        CourseDTO result = mapper.map(testCourse, CourseDTO.class);
        Assertions.assertEquals(testCourse.getName(), result.getName());
        Assertions.assertEquals(testCourse.getDescription(), result.getDescription());
        Assertions.assertEquals(testCourse.getPlan(), result.getPlan());
    }

    @Test
    void convertTask() {
        Task testTask = new Task("test task", "u must choose correct answer", "its ur first task)");

        TaskDTO result = mapper.map(testTask, TaskDTO.class);
        Assertions.assertEquals(testTask.getName(), result.getName());
        Assertions.assertEquals(testTask.getDescription(), result.getDescription());
        Assertions.assertEquals(testTask.getText(), result.getText());
    }
}