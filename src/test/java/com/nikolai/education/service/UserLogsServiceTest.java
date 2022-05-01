package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.redis.RedisService;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserLogsRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableCaching
class UserLogsServiceTest {

    @Mock
    private UserLogsRepo userLogsRepo;
    @Mock
    private OrgRepo orgRepo;
    @Mock
    private CacheManagerService<Logs> cacheManagerService;
    @InjectMocks
    private UserLogsService userLogsService;
    @Mock
    private RedisService redisService;

    private Organization testOrg;
    private User testUser;
    private Logs testLogs;

    @BeforeEach
    void setUp() {
        testOrg = new Organization("English school", "check kwnolage in English", StatusOrgEnum.PUBLIC);
        testOrg.setId(10L);

        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
        testUser.setEnable(false);
        testLogs = new Logs(UserLogsEnum.INVITE, testUser);
        testLogs.setId(1L);
    }

    @Test
    void findAll() {
        List<Logs> list = new ArrayList<>();
        list.add(new Logs(UserLogsEnum.INVITE, testUser));
        list.add(new Logs(UserLogsEnum.CHANGE_STATUS_TASK, testUser));
        list.add(new Logs(UserLogsEnum.DELETE_COURSE, testUser));

        when(redisService.hasKey(anyString())).thenReturn(true);
        when(redisService.lPushAll(anyString(), anyObject())).thenReturn(testLogs.getId());

        when(orgRepo.findByUsers(any(User.class))).thenReturn(testOrg);
        when(userLogsRepo.findAllByUser_Org(any(Organization.class))).thenReturn(list);
        when(cacheManagerService.cachedList(anyString(), anyList())).thenReturn(list);

        userLogsService.findAll(testUser);

    }
}