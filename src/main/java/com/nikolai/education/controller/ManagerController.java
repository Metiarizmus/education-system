package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.payload.request.CourseRequest;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.payload.request.TaskRequest;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.TaskService;
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
public class ManagerController {

    private final CourseService courseService;
    private final TaskService taskService;
    private final SendMessages sendMessages;

    @PostMapping("/create-course")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest, Principal principal) {

        Course course = new Course(courseRequest.getName(), courseRequest.getDescription(), courseRequest.getPlan());
        CourseDTO courseDTO = courseService.createCourse(course, principal);

        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> listCourses(Principal principal) {

        return new ResponseEntity<>(courseService.getAllCourses(principal, TypeRoles.ROLE_MANAGER), HttpStatus.OK);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> coursesById(@PathVariable("id") Long id) {

        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.OK);
    }

    @PostMapping("/create-task")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, @RequestParam("id") Long idCourse) {

        Task task = new Task(taskRequest.getName(), taskRequest.getContent(), taskRequest.getDescription());
        TaskDTO taskDTO = taskService.createTask(task, idCourse, taskRequest.getExpirationCountHours());
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @PostMapping("/invite-to-course/{idCourse}")
    public ResponseEntity<?> inviteUserToCourse(@PathVariable() Long idCourse, @RequestBody InviteRequest inviteRequest,
                                                Principal principal) {


        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvited.MAIL)) {
            String emailSubject = "Invitation to join to the organization on course";
            String mailContent = "Manager invite you to the course ";
            sendMessages.sendInvite(inviteRequest,emailSubject,mailContent,principal, idCourse);
            return new ResponseEntity<>("The invitation has been sent " + inviteRequest.getEmail(),HttpStatus.OK);
        }

        return new ResponseEntity<>("The invitation has been sent " + inviteRequest.getTelephoneNumber(),HttpStatus.OK);
    }


}
