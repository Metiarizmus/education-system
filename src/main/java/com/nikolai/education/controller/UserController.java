package com.nikolai.education.controller;

import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.model.Organization;
import com.nikolai.education.payload.request.OrgRequest;
import com.nikolai.education.service.OrgService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final OrgService orgService;

    @PostMapping("/createOrg")
    public ResponseEntity<?> createOrg(@Valid @RequestBody OrgRequest orgRequest, Principal principal) {
        Organization organization = new Organization(orgRequest.getName(), orgRequest.getDescription(), orgRequest.getStatus());

        OrgDTO org = orgService.createOrg(organization, principal);

        return new ResponseEntity<>(org, HttpStatus.OK);
    }
}
