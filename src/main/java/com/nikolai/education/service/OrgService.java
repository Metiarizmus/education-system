package com.nikolai.education.service;

import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
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
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepo orgRepo;
    private final UserRepo userRepo;


    @Transactional
    public Organization createOrg(Organization organization, User user) {

        user.getRoles().add(new Role(TypeRolesEnum.ROLE_ROOT_ADMIN));
        userRepo.save(user);

        organization.setCreatorId(user.getId());
        organization.setUsers(Collections.singleton(user));

        orgRepo.save(organization);

        return organization;
    }

    public List<Organization> getAllPublicOrg() {
        List<Organization> orgs = orgRepo.findByStatus(StatusOrgEnum.PUBLIC);
        if (orgs.isEmpty()) {
            throw new ResourceNotFoundException("Organization", "public", StatusOrgEnum.PUBLIC);
        }
        return orgs;
    }

    public List<Organization> getAllOrgByCreator(String email) {
        User user = userRepo.findByEmail(email);
        List<Organization> list = orgRepo.findByCreatorId(user.getId());
        return list;
    }

    public List<Organization> getAllOrgByEmailAndRole(String roleName, String email) {

        List<Organization> list = orgRepo.findByUserEmailAndRole(roleName, email);
        return list;
    }

    public List<Organization> getAllPublicOrgByName(String name) {
        List<Organization> orgs = orgRepo.findByStatusAndNameLike(StatusOrgEnum.PUBLIC, name);
        if (orgs.isEmpty()) {
            throw new ResourceNotFoundException("Organization", "public", StatusOrgEnum.PUBLIC);
        }
        return orgs;
    }

    public Organization getOrgById(Long idOrg) {
        Optional<Organization> org = orgRepo.findById(idOrg);
        if (org.isPresent()) {
            return org.get();
        } else throw new ResourceNotFoundException("Organization", "id", idOrg);
    }

    public Organization joinInPublicOrg(Long idOrg, User user) {
        Optional<Organization> org = orgRepo.findById(idOrg);
        if (org.isPresent() && user != null) {
            org.get().getUsers().add(user);
            orgRepo.save(org.get());
            return org.get();
        } else throw new ResourceNotFoundException("Organization", "id", idOrg);
    }

    public void deleteOrg(User user) {
        Organization org = orgRepo.findByUsers(user);
        if (org != null) {
            orgRepo.delete(org);
        } else throw new ResourceNotFoundException("Organization", "user", user);

    }

}
