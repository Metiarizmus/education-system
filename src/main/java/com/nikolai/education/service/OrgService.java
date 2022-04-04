package com.nikolai.education.service;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.StatusOrg;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepo orgRepo;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;

    @Transactional
    public OrgDTO createOrg(Organization organization, User user) {

        Role role = new Role(TypeRoles.ROLE_ROOT_ADMIN);
        user.getRoles().add(role);

        organization.setCreatorId(user.getId());
        organization.setUsers(Collections.singleton(user));

        userRepo.save(user);
        orgRepo.save(organization);

        return convertDto.convertOrg(organization);
    }

    public List<OrgDTO> findAllPublicOrg() {
        List<Organization> orgs = orgRepo.findByStatus(StatusOrg.PUBLIC);
        return orgs.stream().map(convertDto::convertOrg).collect(Collectors.toList());
    }

    public OrgDTO findOrgById(Long idOrg) {
        Optional<Organization> org = orgRepo.findById(idOrg);
        return (OrgDTO) org.stream().map(convertDto::convertOrg).collect(Collectors.toList());
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
