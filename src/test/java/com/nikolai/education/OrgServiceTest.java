package com.nikolai.education;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.util.ConvertDto;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class OrgServiceTest {

    @InjectMocks
    private OrgService orgService;

    @Mock
    private UserRepo userRepo;
    @Mock
    private OrgRepo orgRepo;
    @InjectMocks
    private static ConvertDto convertDto;

    private static Organization testOrg;
    private static User testUser;
    private static OrgDTO testOrgDto;

    @BeforeClass
    public static void prepareTestData() {
        testOrg = new Organization();
        testOrg.setId(10L);
        testOrg.setName("English school");
        testOrg.setDescription("check kwnolage in English");
        testOrg.setStatus(StatusOrgEnum.PUBLIC);

        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");

        testOrgDto = convertDto.convertOrg(testOrg);
    }

    @Test
    public void createOrgTest() {

        testOrg = new Organization();
        testOrg.setId(10L);
        testOrg.setName("English school");
        testOrg.setDescription("check kwnolage in English");
        testOrg.setStatus(StatusOrgEnum.PUBLIC);

        testUser = new User("Nikolai", "Nagornykh", "stormytt@mail.ru",
                "112233", "+375444321965");

        boolean orgDTO = orgService.createOrg(testOrg, testUser);

        Assert.assertTrue(orgDTO);
        Assert.assertNotNull(testOrg.getUsers());
        Assert.assertNotNull(testOrg.getDateCreated());

        Mockito.verify(userRepo, Mockito.times(1)).save(testUser);
        Mockito.verify(orgRepo, Mockito.times(1)).save(testOrg);
    }

//    @Test
//    void getAllPublicOrg() {
//        List<Organization> testList = new ArrayList<>();
//        testList.add(new Organization("one", "one", StatusOrg.PUBLIC));
//        testList.add(new Organization("two", "two", StatusOrg.PUBLIC));
//        testList.add(new Organization("three", "three", StatusOrg.PUBLIC));
//    }

    @Test
    void getOrgById() {
        //when(orgRepo.findById(any(Long.class))).thenReturn(Optional.of(testOrg));
        OrgDTO mockDtoOrg = new OrgDTO(1L,"one","descr", StatusOrgEnum.PUBLIC,
                Collections.singleton(new User()), Collections.singleton(new Course()));

        doReturn(testOrg).when(orgRepo).findById(10L);

        //Mockito.when(orgService.getOrgById(Mockito.anyLong())).thenReturn(mockDtoOrg);

        //Assert.assertSame(resultOrg.getId(), testOrg.getId());
        //Assert.assertEquals(resultOrg, testOrgDto);
    }

    @Test
    void joinInPublicOrg() {
    }

    @Test
    void deleteOrg() {
    }

}
