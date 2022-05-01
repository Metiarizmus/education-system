package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.UserLogsService;
import com.nikolai.education.service.UserService;
import com.nikolai.education.util.ConvertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin controller", description = "points for control users, managers and courses")
public class AdminController {

    private final CourseService courseService;
    private final UserService userService;
    private final SendMessages sendMessages;
    private final UserLogsService logsService;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;

    @Operation(
            summary = "Get list of courses in the organization"
    )
    @GetMapping("/courses")
    public List<CourseDTO> getAllCourses(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = courseService.getAllCourses(user, TypeRolesEnum.ROLE_ADMIN);
        List<CourseDTO> courseDTOS = list.stream().map(convertDto::convertCourse).collect(Collectors.toList());
        return courseDTOS;
    }

    @Operation(
            summary = "Get list of users in the organization"
    )
    @GetMapping("/users")
    public List<UserDTO> getAllUsers(Principal principal, @RequestParam("type") TypeRolesEnum typeRolesEnum) {
        User user = userRepo.findByEmail(principal.getName());

        List<User> list = null;
        if (TypeRolesEnum.ROLE_USER.equals(typeRolesEnum)) {
            list = userService.getAllUsersInOrg(user, TypeRolesEnum.ROLE_USER);
        } else if (TypeRolesEnum.ROLE_MANAGER.equals(typeRolesEnum)) {
            list = userService.getAllUsersInOrg(user, TypeRolesEnum.ROLE_MANAGER);
        }

        List<UserDTO> dtos = list.stream().map(convertDto::convertUser).collect(Collectors.toList());
        return dtos;
    }

    @GetMapping("/users/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        UserDTO userDTO = convertDto.convertUser(userService.getById(id));
        return userDTO;
    }

    @Operation(
            summary = "Invite users to the organization"
    )
    @PostMapping("/invite")
    public ResponseEntity<HttpStatus> inviteUserToOrg(@RequestBody InviteRequest inviteRequest, Principal principal) {


        String emailSubject = "Invitation to join to the organization";
        String content = "Admin invite you to the organization ";

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvitedEnum.MAIL)) {
            sendMessages.sendInvite(inviteRequest, emailSubject, content, principal.getName(), null);
            return new ResponseEntity<>(HttpStatus.OK);
        } else if (inviteRequest.getTypeWayInvited().equals(TypeWayInvitedEnum.TELEGRAM)) {
            sendMessages.sendInvite(inviteRequest, null, content, principal.getName(), null);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get users actions in the system"
    )
    @GetMapping("/logs")
    public List<?> getLogs(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<?> list = logsService.findAll(user);
        return list;
    }

}