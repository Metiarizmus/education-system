package com.nikolai.education.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.model.*;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.service.TaskService;
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
@WithMockUser(roles = "USER")
class UserControllerTest {

    @MockBean
    private OrgService orgService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ConvertDto convertDto;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private CourseService courseService;
    @MockBean
    private ConfirmTokenRepo confirmTokenRepo;
    @MockBean
    private TaskService taskService;

    private User testUser;
    private Organization testOrg;
    private Course testCourse;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testOrg = new Organization("English shoole test org", "powerful learning", StatusOrgEnum.PRIVATE);
        testCourse = new Course("one", "descr1", "plan for first");
        testTask = new Task("control task", "its ur first task in this course", "control");

        testUser = new User("user", "user", "user@mail.ru", "111", "+7946283514");

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createOrg() throws Exception {

        when(userRepo.findByEmail(anyString())).thenReturn(testUser);

        mockMvc.perform(post("/api/users/create-org")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(convertDto.convertOrg(testOrg)))
                )
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isCreated());
    }

    @Test
    void getCourses() throws Exception {
        List<Course> list = new ArrayList<>();
        list.add(new Course("one", "descr1", "plan for first"));
        list.add(new Course("two", "descr2", "plan for second"));
        list.add(new Course("last", "descr3", "plan for last"));

        when(userRepo.findByEmail(anyString())).thenReturn(testUser);
        when(courseService.getAllCourses(any(User.class), any(TypeRolesEnum.class))).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/courses")
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
    void getCourseById() throws Exception {
        when(courseService.getCourseById(anyLong())).thenReturn(testCourse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/courses/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("one"));
    }

    @Test
    void acceptCourse() throws Exception {
        User testUserInDb = new User();
        testUserInDb.setEmail("newUserTest2@mail.ru");

        String confirmTokenTest = "3975bb3e-6e21-4078-83b7-1dd7d81c342d";

        InvitationLink token = new InvitationLink(testUserInDb, 4, 2L, TypeWayInvitedEnum.MAIL);

        when(confirmTokenRepo.findByConfirmationToken(anyString())).thenReturn(token);
        when(courseService.acceptedCourse(anyString(), anyLong(), anyLong())).thenReturn(testCourse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/accept-course")
                        .param("confirmToken", confirmTokenTest)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("one"));

    }

    @Test
    void startCourse() throws Exception {
        when(userRepo.findByEmail(anyString())).thenReturn(testUser);

        when(courseService.startCourse(anyLong(), any(User.class))).thenReturn(testTask);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/start-courses/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("control"));
    }

    @Test
    void changeStatusTask() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/change-status-task/{id}", 1)
                        .param("status", String.valueOf(ProgressTaskEnum.IN_PROGRESS))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());
    }

    @Test
    void findAllPublicOrgs() throws Exception {
        List<Organization> testListOrgs = new ArrayList<>();
        testListOrgs.add(new Organization("one","one",StatusOrgEnum.PUBLIC));
        testListOrgs.add(new Organization("two","two",StatusOrgEnum.PUBLIC));
        testListOrgs.add(new Organization("three","three",StatusOrgEnum.PUBLIC));

        when(orgService.getAllPublicOrg()).thenReturn(testListOrgs);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/public-orgs")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").isNotEmpty())
                .andExpect(jsonPath("$[*].description").isNotEmpty())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void NoFindPublicOrgs() throws Exception {

        when(orgService.getAllPublicOrg()).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/public-orgs")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isNoContent());
    }

    @Test
    void findPublicOrgById() throws Exception {
        when(orgService.getOrgById(anyLong())).thenReturn(new Organization("one","one",StatusOrgEnum.PUBLIC));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/public-orgs/{id}",1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("one"));

    }

    @Test
    void joinPublicOrgById() throws Exception {

        when(userRepo.findByEmail(anyString())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/join-public-orgs/{id}",1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk());

    }
}