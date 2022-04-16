package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepo orgRepo;
    private final UserRepo userRepo;


    @Transactional
    public boolean createOrg(Organization organization, User user) {

        user.getRoles().add(new Role(TypeRolesEnum.ROLE_ROOT_ADMIN));
        organization.setCreatorId(user.getId());
        organization.setUsers(Collections.singleton(user));

        orgRepo.save(organization);
        userRepo.save(user);


        return true;
    }

    public List<Organization> getAllPublicOrg() {
        List<Organization> orgs = orgRepo.findByStatus(StatusOrgEnum.PUBLIC);
        if (orgs.isEmpty()) {
            return null;
        }
        return orgs;
    }

    public Organization getOrgById(Long idOrg) {
        Organization org = orgRepo.findById(idOrg).orElse(null);
        return org;
    }

    public void joinInPublicOrg(Long idOrg, User user) {
        Organization organization = orgRepo.getById(idOrg);
        organization.getUsers().add(user);
        orgRepo.save(organization);
    }

    public void deleteOrg(User user) {
        Organization org = orgRepo.findByUsers(user);
        orgRepo.delete(org);
    }

}
