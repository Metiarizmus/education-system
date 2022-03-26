package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin controller", description = "points for control users, managers and courses")
public class AdminController {

    private final CourseService courseService;
    private final UserService userService;
    private final SendMessages sendMessages;

    @Operation(
            summary = "Get list of courses in the organization"
    )
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses(Principal principal) {
        return new ResponseEntity<>(courseService.getAllCourses(principal, TypeRoles.ROLE_ADMIN), HttpStatus.OK);
    }

    @Operation(
            summary = "Get list of users in the organization"
    )
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(Principal principal) {
        List<UserDTO> dtos = userService.getAllUsersInOrg(principal, TypeRoles.ROLE_USER);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @Operation(
            summary = "Get list of managers in the organization"
    )
    @GetMapping("/managers")
    public ResponseEntity<List<UserDTO>> getAllManager(Principal principal) {
        List<UserDTO> dtos = userService.getAllUsersInOrg(principal, TypeRoles.ROLE_MANAGER);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @Operation(
            summary = "Invite users to the organization"
    )
    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserToOrg(@RequestBody InviteRequest inviteRequest, Principal principal) {

        String emailSubject = "Invitation to join to the organization";
        String mailContent = "Admin invite you to the organization ";

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {
            sendMessages.sendInvite(inviteRequest,emailSubject,mailContent,principal, null);
            return new ResponseEntity<>("The invitation has been sent to email " + inviteRequest.getEmail(),HttpStatus.OK);
        }

        return new ResponseEntity<>("The invitation has been sent to telegram with number " + inviteRequest.getTelephoneNumber(),HttpStatus.OK);
    }


}
