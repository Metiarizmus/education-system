package com.nikolai.education.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.RefreshToken;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.RefreshTokenRequest;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.security.jwt.JwtUtils;
import com.nikolai.education.service.RefreshTokenService;
import com.nikolai.education.service.UserService;
import com.nikolai.education.util.ConvertDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private ConfirmTokenRepo confirmTokenRepo;
    @MockBean
    private UserService userService;
    @MockBean
    private SendMessages sendMessage;
    @Autowired
    private ConvertDto convertDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private RefreshTokenService refreshTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void registerUser() throws Exception {
        UserDTO registrTest = new UserDTO("NewUser", "NewUser", "newUserTest2@mail.ru", "123", "+375444294632");
        registrTest.setRoles(Collections.singleton(new Role(TypeRolesEnum.ROLE_USER)));

        when(userRepo.existsByEmail(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(registrTest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

    }

    @Test
    void registerExistUser() throws Exception {
        UserDTO registrTest = new UserDTO("NewUser", "NewUser", "newUserTest2@mail.ru", "123", "+375444294632");
        registrTest.setRoles(Collections.singleton(new Role(TypeRolesEnum.ROLE_USER)));

        when(userRepo.existsByEmail(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                .content(objectMapper.writeValueAsString(registrTest))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

    }

    @Test
    void inviteRegistr() throws Exception {
        UserDTO registrTest = new UserDTO("NewUser", "NewUser", "newUserTest2@mail.ru", "123", "+375444294632");

        User testUserInDb = new User();
        testUserInDb.setEmail("newUserTest2@mail.ru");
        testUserInDb.setId(2L);
        String confirmTokenTest = "3975bb3e-6e21-4078-83b7-1dd7d81c342d";

        InvitationLink token = new InvitationLink(testUserInDb, 4, 2L, TypeWayInvitedEnum.MAIL);
        token.setIdCourse(1L);

        when(confirmTokenRepo.findByConfirmationToken(anyString())).thenReturn(token);
        when(userRepo.existsByEmail(anyString())).thenReturn(true);
        when(userRepo.getById(anyLong())).thenReturn(testUserInDb);
        when(sendMessage.validLink(anyString())).thenReturn(true);


        mockMvc.perform(post("/api/auth/signup/invite")
                .content(objectMapper.writeValueAsString(registrTest))
                .param("confirmToken", confirmTokenTest)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is(200));

    }

    @Test
    void authenticateUser() throws Exception {
        String token = "3975bb3e-6e21-4078-83b7-1dd7d81c342d";

        String jwt = "asdssd8yth-asdxmvcxcn-asda";

        RefreshToken refreshToken = new RefreshToken(1L, new User("user", "user", "user@mail.ru",
                "123", "8431214444"), token, 3L);

        UserDTO loginRequest = new UserDTO("", "", "test@mail.ru", "1234", "");

        when(jwtUtils.generateJwtToken(any())).thenReturn(jwt);
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);


        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200)).andReturn();
    }

    @Test
    void refreshtoken() throws Exception {
        String token = "3975bb3e-6e21-4078-83b7-1dd7d81c342d";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("asdssd8yth-asdxmvcxcn-asda");
        RefreshToken refreshToken = new RefreshToken(1L, new User("user", "user", "user@mail.ru",
                "123", "8431214444"), token, 3L);

        when(refreshTokenService.findByToken(anyString())).thenReturn(java.util.Optional.of(refreshToken));
        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is(200)).andReturn();
    }
}