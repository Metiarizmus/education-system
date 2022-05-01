package com.nikolai.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.UserLogsService;
import com.nikolai.education.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @MockBean
    private CourseRepo courseRepo;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CourseService courseService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private UserService userService;
    @MockBean
    private UserLogsService userLogsService;
    @MockBean
    private SendMessages sendMessages;
    @Autowired
    private ObjectMapper objectMapper;

    private User testUSer;

    @BeforeEach
    public void setUp() {
        testUSer = new User("admin", "admin", "testAdmin@mail.ri", "123", "3337778954");

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getAllCourses() throws Exception {
        List<Course> list = new ArrayList<>();
        list.add(new Course("one", "descr1", "plan for first"));
        list.add(new Course("two", "descr2", "plan for second"));
        list.add(new Course("last", "descr3", "plan for last"));

        when(userRepo.findByEmail(anyString())).thenReturn(testUSer);
        when(courseService.getAllCourses(any(User.class), any(TypeRolesEnum.class))).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/courses")
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
    void getAllUsers() throws Exception {
        List<User> testList = new ArrayList<>();
        testList.add(new User("first", "last", "first@mail.ru", "112233", "5469888"));
        testList.add(new User("first1", "last1", "first1@mail.ru", "112233", "546955888"));
        testList.add(new User("first2", "last2", "first2@mail.ru", "112233", "5478969888"));

        when(userRepo.findByEmail(anyString())).thenReturn(testUSer);
        when(userService.getAllUsersInOrg(any(User.class), any(TypeRolesEnum.class))).thenReturn(testList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/users")
                        .param("type", String.valueOf(TypeRolesEnum.ROLE_USER))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email").isNotEmpty())
                .andExpect(jsonPath("$[0].firstName").value("first"))
                .andExpect(jsonPath("$", hasSize(3)));
    }


    @Test
    void getUserById() throws Exception {
        User testUser = new User("first", "last", "first@mail.ru", "112233", "5469888");

        when(userService.getById(anyLong())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/users/{id}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("firstName").value("first"))
                .andReturn();

    }

    @Test
    void inviteUserToOrg() throws Exception {

        InviteRequest inviteRequest = new InviteRequest("newUser@mail.ru", "", TypeRolesEnum.ROLE_USER, 3, TypeWayInvitedEnum.MAIL);

        mockMvc.perform(post("/api/admin/invite")
                        .content(objectMapper.writeValueAsString(inviteRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    void getLogs() throws Exception {
        User testUser = new User("first", "last", "first@mail.ru", "112233", "5469888");

        List<Logs> list = new ArrayList<>();
        list.add(new Logs(UserLogsEnum.INVITE, testUser));
        list.add(new Logs(UserLogsEnum.CHANGE_STATUS_TASK, testUser));
        list.add(new Logs(UserLogsEnum.DELETE_COURSE, testUser));

        when(userRepo.findByEmail(anyString())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/logs")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

    }
}