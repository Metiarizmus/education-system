package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrgServiceTest {

    @InjectMocks
    private OrgService orgService;

    @Mock
    private UserRepo userRepo;
    @Mock
    private OrgRepo orgRepo;

    private Organization testOrg;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testOrg = new Organization("English school", "check kwnolage in English", StatusOrgEnum.PUBLIC);
        testOrg.setId(10L);

        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");
    }

    @Test
    public void createOrg() {

        Organization org = orgService.createOrg(testOrg, testUser);

        Assertions.assertNotNull(org);
        Assertions.assertNotNull(testOrg.getUsers());
        Assertions.assertNotNull(testOrg.getDateCreated());
        testUser.setRoles(Collections.singleton(new Role(TypeRolesEnum.ROLE_ROOT_ADMIN)));
        Assertions.assertEquals(org.getUsers(), Collections.singleton(testUser));

        Mockito.verify(userRepo, Mockito.times(1)).save(testUser);
        Mockito.verify(orgRepo, Mockito.times(1)).save(testOrg);
    }

    @Test
    void getAllPublicOrg() {
        List<Organization> testList = new ArrayList<>();
        testList.add(new Organization("one", "one", StatusOrgEnum.PUBLIC));
        testList.add(new Organization("two", "two", StatusOrgEnum.PUBLIC));
        testList.add(new Organization("three", "three", StatusOrgEnum.PUBLIC));

        when(orgService.getAllPublicOrg()).thenReturn(testList);
        List<Organization> result = orgService.getAllPublicOrg();
        Assertions.assertEquals(3, result.size());
        Mockito.verify(orgRepo, Mockito.times(1)).findByStatus(StatusOrgEnum.PUBLIC);
    }

    @Test
    void getAllPublicOrgException() {
        assertThatThrownBy(() -> orgService.getAllPublicOrg()).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getOrgById() {

        when(orgRepo.findById(any(Long.class))).thenReturn(Optional.of(testOrg));

        Organization org = orgService.getOrgById(1L);

        Assertions.assertEquals("English school", org.getName());
        Assertions.assertEquals(Long.valueOf(10), org.getId());
        Assertions.assertEquals("check kwnolage in English", org.getDescription());
        Assertions.assertEquals(StatusOrgEnum.PUBLIC, org.getStatus());

        Mockito.verify(orgRepo, Mockito.times(1)).findById(1L);

    }

    @Test
    void getOrgByIdException() {
        assertThatThrownBy(() -> orgService.getOrgById(null)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void joinInPublicOrg() {

        when(orgRepo.findById(any(Long.class))).thenReturn(Optional.ofNullable(testOrg));

        Organization org = orgService.joinInPublicOrg(1L, testUser);

        Assertions.assertNotNull(org);
        Assertions.assertEquals("English school", org.getName());
        Assertions.assertEquals(Collections.singleton(testUser), org.getUsers());

        Mockito.verify(orgRepo, Mockito.times(1)).findById(1L);
        Mockito.verify(orgRepo, Mockito.times(1)).save(testOrg);
    }

    @Test
    void joinInPublicOrgException() {
        assertThatThrownBy(() -> orgService.joinInPublicOrg(null, testUser)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> orgService.joinInPublicOrg(1L, null)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> orgService.joinInPublicOrg(null, null)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteOrg() {
        testOrg.setUsers(Collections.singleton(testUser));
        when(orgRepo.findByUsers(any(User.class))).thenReturn(testOrg);
        orgService.deleteOrg(testUser);

        Mockito.verify(orgRepo, Mockito.times(1)).delete(testOrg);
    }

}
