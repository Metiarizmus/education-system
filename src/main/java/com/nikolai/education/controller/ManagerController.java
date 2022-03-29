package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.TaskService;
import com.nikolai.education.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@Tag(name = "Manager controller", description = "points for courses and tasks in the organization")
public class ManagerController {

    private final CourseService courseService;
    private final TaskService taskService;
    private final SendMessages sendMessages;
    private final UserService userService;

    @Operation(
            summary = "Create course for a particular organization"
    )
    @PostMapping("/create-course")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseDTO courseRequest, Principal principal) {

        Course course = new Course(courseRequest.getName(), courseRequest.getDescription(), courseRequest.getPlan());
        CourseDTO courseDTO = courseService.createCourse(course, principal);

        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "List of courses for a particular manager"
    )
    @GetMapping("/courses")
    public ResponseEntity<List<? extends Object>> listCourses(Principal principal) {
        return new ResponseEntity<>(courseService.getAllCourses(principal, TypeRoles.ROLE_MANAGER), HttpStatus.OK);
    }

    @Operation(
            summary = "Get course by id"
    )
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> coursesById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Create task for a particular course"
    )
    @PostMapping("/create-task")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDTO taskRequest, @RequestParam("id") Long idCourse) {

        Task task = new Task(taskRequest.getName(), taskRequest.getContent(), taskRequest.getDescription());
        TaskDTO taskDTO = taskService.createTask(task, idCourse, taskRequest.getExpirationCountHours());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Send an invitation to join a course"
    )
    @PostMapping("/invite-course/{idCourse}")
    public ResponseEntity<?> inviteUserToCourse(@PathVariable() Long idCourse, @RequestBody InviteRequest inviteRequest,
                                                Principal principal) {
        String link = null;

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {
            String emailSubject = "Invitation to join to the organization on course";
            String mailContent = "Manager invite you to the course ";
            sendMessages.sendInvite(inviteRequest, emailSubject, mailContent, principal, idCourse);
            return new ResponseEntity<>("The invitation has been sent " + inviteRequest.getEmail(), HttpStatus.OK);
        } else if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.TELEGRAM)) {
            link = sendMessages.sendInvite(inviteRequest, null, null, principal, idCourse);
        }

        return new ResponseEntity<>("Give this link for invite user : " + link, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user from a course"
    )
    @DeleteMapping("/delete-user/{idCourse}/{idUser}")
    public ResponseEntity<?> deleteUserFromCourse(@PathVariable("idCourse") Long idCourse,
                                                  @PathVariable("idUser") Long idUser) {

        userService.deleteUserFromCourse(idCourse, idUser);

        return new ResponseEntity<>("User was deleted from the course", HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a course"
    )
    @DeleteMapping("/delete-course/{idCourse}")
    public ResponseEntity<?> deleteCourse(@PathVariable("idCourse") Long idCourse) {

        courseService.deleteCourse(idCourse);

        return new ResponseEntity<>("Course was deleted", HttpStatus.OK);
    }


}
