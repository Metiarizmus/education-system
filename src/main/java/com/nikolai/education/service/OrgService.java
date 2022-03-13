package com.nikolai.education.service;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.RoleRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrgService {

    private final OrgRepo orgRepo;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;
    private final RoleRepo roleRepo;

    @Transactional
    public OrgDTO createOrg(Organization organization, Principal principal) {

        Optional<User> user = Optional.ofNullable(userRepo.findByEmail(principal.getName()));
        Role role = new Role(TypeRoles.ROLE_ROOT_ADMIN);
        user.get().getRoles().add(role);

        organization.setCreatorId(user.get().getId());
        organization.setUsers(Collections.singleton(user.get()));

        userRepo.save(user.get());
        orgRepo.save(organization);

        return convertDto.convertOrg(organization);
    }
}
