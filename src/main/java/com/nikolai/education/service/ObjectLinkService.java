package com.nikolai.education.service;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.model.ObjectLink;
import com.nikolai.education.repository.ObjectLinkRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObjectLinkService {

    private final ObjectLinkRepo linkObjectRepo;
    private final UserService userService;

    public void saveLinkObject(ObjectLink objectLink, TypeRoles typeRoles) {
        userService.saveUser(objectLink.getUser(), typeRoles);
        linkObjectRepo.save(objectLink);
    }
}
