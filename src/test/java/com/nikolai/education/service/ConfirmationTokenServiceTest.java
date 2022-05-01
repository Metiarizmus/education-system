package com.nikolai.education.service;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmTokenRepo confirmTokenRepo;
    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserService userService;

    @Test
    void saveConfirmTokenUserExist() {

        User testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testUser.setEnable(false);

        InvitationLink link = new InvitationLink(testUser, 3, 1L, TypeWayInvitedEnum.MAIL);
        link.setUser(testUser);

        when(userRepo.existsByEmail(anyString())).thenReturn(false);

        confirmationTokenService.saveConfirmToken(link, TypeRolesEnum.ROLE_USER);

        Mockito.verify(userService, Mockito.times(1)).saveUser(link.getUser());
        Mockito.verify(confirmTokenRepo, Mockito.times(1)).save(link);
    }

}