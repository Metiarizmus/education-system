package com.nikolai.education.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.StatusOrgEnum;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.util.ConvertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/orgs")
@Tag(name = "Org controller")
public class OrgController {
    private final OrgService orgService;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Create a organization"
    )
    @PostMapping(value = "/create-org")
    public ResponseEntity<?> createOrg(@RequestParam(name = "org") String orgJson,
                                       @RequestParam("file") MultipartFile image,
                                       Principal principal
    ) throws IOException {

        JSONObject json = null;
        String statusOrg = null;
        try {
            json = new JSONObject(orgJson);
            statusOrg = json.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (image == null) {
            return new ResponseEntity<>("Error", HttpStatus.NOT_FOUND);
        }
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Organization organization = objectMapper.readValue(orgJson, Organization.class);
        organization.setStatus(StatusOrgEnum.valueOf(statusOrg));
        organization.setAvatar(image.getBytes());

        User user = userRepo.findByEmail(principal.getName());
        orgService.createOrg(organization, user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all public organizations in the system"
    )
    @GetMapping("/public-orgs")
    public ResponseEntity<?> findAllPublicOrgs(@RequestParam("name") String name) {

        List<Organization> list;
        if (name.isEmpty()) {
            list = orgService.getAllPublicOrg();
        } else list = orgService.getAllPublicOrgByName(name);

        return getResponseEntity(list);
    }


    private ResponseEntity<?> getResponseEntity(List<Organization> list) {
        if (list == null) {
            return new ResponseEntity<>("Not have public organizations", HttpStatus.NO_CONTENT);
        } else {
            List<OrgDTO> orgDTOS = list.stream().map(convertDto::convertOrg).collect(Collectors.toList());
            return new ResponseEntity<>(orgDTOS, HttpStatus.OK);
        }
    }

    @Operation(
            summary = "Get all org for creator"
    )
    @GetMapping("/orgs-creator")
    public ResponseEntity<?> findAllOrgsByCreator(Principal principal) {

        List<Organization> list = orgService.getAllOrgByCreator(principal.getName());

        return getResponseEntity(list);
    }

    @GetMapping("/public-orgs/{id}")
    public OrgDTO findPublicOrgById(@PathVariable Long id) {
        return convertDto.convertOrg(orgService.getOrgById(id));
    }

}
