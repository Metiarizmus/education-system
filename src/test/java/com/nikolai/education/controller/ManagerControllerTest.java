package com.nikolai.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.TaskService;
import com.nikolai.education.service.UserService;
import com.nikolai.education.util.ConvertDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "MANAGER")
class ManagerControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CourseService courseService;
    @MockBean
    private TaskService taskService;
    @Autowired
    private ConvertDto convertDto;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private SendMessages sendMessages;
    @MockBean
    private UserService userService;

    private User user;
    private Course course;


    @BeforeEach
    void setUp() {
        user = new User("test", "test", "test@mail.ru", "111", "+37544479654");
        user.setId(1L);
        course = new Course("test course", "descrt course", "plan course");


        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createCourse() throws Exception {

//        Course newCourse = new Course("a", "b", "c");
//        newCourse.setCreatorId(user.getId());
//
//     //   when(courseService.createCourse(course, user, anyList())).thenReturn(newCourse);
//
//        mockMvc.perform(post("/api/managers/create-course")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(convertDto.convertCourse(course)))
//                )
//                .andDo(print())
//                .andExpect(authenticated())
//                .andExpect(status().is(200));

    }

    @Test
    void listCourses() throws Exception {
        List<Course> list = new ArrayList<>();
        list.add(new Course("one", "descr1", "plan for first"));
        list.add(new Course("two", "descr2", "plan for second"));
        list.add(new Course("last", "descr3", "plan for last"));

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(courseService.getAllCourses(any(User.class), any(TypeRolesEnum.class))).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/managers/courses")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").isNotEmpty())
                .andExpect(jsonPath("$[*].description").isNotEmpty())
                .andExpect(jsonPath("$[*].plan").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void coursesById() throws Exception {

        Course newCourse = new Course("a", "b", "c");

        when(courseService.getCourseById(anyLong())).thenReturn(newCourse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/managers/courses/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("a"))
                .andReturn();
    }

    @Test
    void createTask() throws Exception {
        Task taskRequest = new Task("task", "task", "awesome task");
        taskRequest.setExpirationCountHours(72);

        Task taskRequest1 = new Task("task", "task", "awesome task");
        taskRequest.setExpirationCountHours(72);

        when(taskService.createTask(taskRequest, 1L, taskRequest.getExpirationCountHours())).thenReturn(taskRequest1);
        mockMvc.perform(post("/api/managers/courses/{id}/create-task", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(convertDto.convertTask(taskRequest)))
                )
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200));

    }

    @Test
    void inviteUserToCourse() throws Exception {
        InviteRequest inviteRequest = new InviteRequest("newUser@mail.ru", "", TypeRolesEnum.ROLE_USER, 3, TypeWayInvitedEnum.MAIL);

        mockMvc.perform(post("/api/managers/invite-course/{idCourse}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inviteRequest))
                )
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200));
    }

    @Test
    void deleteUserFromCourse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/managers/delete-user/{idCourse}/{idUser}", 1,1) )
                .andExpect(status().isAccepted());
    }

    @Test
    void deleteCourse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/managers/delete-course/{idCourse}", 1) )
                .andExpect(status().isAccepted());
    }
}