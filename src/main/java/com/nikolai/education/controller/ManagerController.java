package com.nikolai.education.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.payload.request.InviteRequest;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.service.TaskService;
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
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RequestMapping("/api/managers")
@RequiredArgsConstructor
@Tag(name = "Manager controller", description = "points for courses and tasks in the organization")
public class ManagerController {

    private final CourseService courseService;
    private final TaskService taskService;
    private final SendMessages sendMessages;
    private final UserService userService;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;
    private final OrgService orgService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Create course for a particular organization"
    )
    @PostMapping("/create-course/{idOrg}")
    public CourseDTO createCourse(@RequestParam("course") String courseRequest,
                                  Principal principal,
                                  @PathVariable("idOrg") Long idOrg) throws JsonProcessingException {

        Course course = objectMapper.readValue(courseRequest, Course.class);

        User user = userRepo.findByEmail(principal.getName());
        Course createdCourse = courseService.createCourse(course, user, idOrg);


        return convertDto.convertCourse(createdCourse);
    }

    private ResponseEntity<?> getResponseEntity(List<Organization> list) {
        if (list == null) {
            return new ResponseEntity<>("Not have public organizations", HttpStatus.NO_CONTENT);
        } else {
            List<OrgDTO> orgDTOS = list.stream().map(convertDto::convertOrg).collect(Collectors.toList());
            return new ResponseEntity<>(orgDTOS, HttpStatus.OK);
        }
    }

    @GetMapping("/orgs/{role}")
    public ResponseEntity<?> findAllByManager(@PathVariable("role") String role, Principal principal) {

        List<Organization> list = orgService.getAllOrgByEmailAndRole(role, principal.getName());
        return getResponseEntity(list);
    }

    @Operation(
            summary = "List of courses for a particular manager"
    )
    @GetMapping("/courses")
    public ResponseEntity<List<?>> listCourses(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = courseService.getAllCourses(user, TypeRolesEnum.ROLE_MANAGER);
        List<CourseDTO> courseDTOS = list.stream().map(convertDto::convertCourse).collect(Collectors.toList());
        return new ResponseEntity<>(courseDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Get course by id"
    )
    @GetMapping("/courses/{id}")
    public ResponseEntity<?> coursesById(@PathVariable("id") Long id) {

        return new ResponseEntity<>(convertDto.convertCourse(courseService.getCourseById(id)), HttpStatus.OK);

    }

    @PutMapping("/update-course")
    public ResponseEntity<Course> updateEmployee(@RequestBody Course course) {

        Course updateCourse = courseService.updateCourse(course);

        return new ResponseEntity<>(updateCourse, HttpStatus.OK);
    }

    @DeleteMapping("/task/{id}")
    public Long deleteTask(@PathVariable("id") Long id, Principal principal) {
        taskService.deleteTask(id, principal.getName());
        return id;
    }

    @Operation(
            summary = "Create task for a particular course"
    )
    @PostMapping("courses/{id}/create-task")
    public ResponseEntity<?> createTask(@RequestParam(name = "task") String taskJson,
                                        @PathVariable("id") Long idCourse) throws JsonProcessingException {

        Task task = objectMapper.readValue(taskJson, Task.class);

        Task createdTask = taskService.createTask(task, idCourse, task.getExpirationCountHours());

        TaskDTO taskDTO = convertDto.convertTask(createdTask);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @GetMapping("/status-tasks-users/{id}/{email}")
    public List<TaskDTO> getTasks(@PathVariable("email") String email,
                                  @PathVariable("id") Long id) {

        List<Task> list = taskService.listTaskForUser(email, id);
        List<TaskDTO> tasksDTOS = list.stream().map(convertDto::convertTask).collect(Collectors.toList());
        return tasksDTOS;
    }

    @Operation(
            summary = "Send an invitation to join a course"
    )
    @PostMapping("/invite-to-course/{idCourse}")
    public ResponseEntity<?> inviteUserToCourse(@PathVariable() Long idCourse, @RequestBody InviteRequest inviteRequest,
                                                Principal principal) {

        String emailSubject = "Invitation to join to the organization on course";
        String content = "Manager invite you to the course ";

        if (inviteRequest.getTypeWayInvited().equals(TypeWayInvitedEnum.MAIL)) {
            sendMessages.sendInvite(inviteRequest, emailSubject, content, principal.getName(), idCourse);
            return new ResponseEntity<>("The invitation has been sent " + inviteRequest.getEmail(), HttpStatus.OK);
        } else if (inviteRequest.getTypeWayInvited().equals(TypeWayInvitedEnum.TELEGRAM)) {
            sendMessages.sendInvite(inviteRequest, null, content, principal.getName(), idCourse);
        }

        return new ResponseEntity<>("The invitation has been sent to telegram " + inviteRequest.getTelephoneNumber(), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user from a course"
    )
    @DeleteMapping("/delete-user/{idCourse}/{idUser}")
    public ResponseEntity<HttpStatus> deleteUserFromCourse(@PathVariable("idCourse") Long idCourse,
                                                           @PathVariable("idUser") Long idUser) {

        userService.deleteUserFromCourse(idCourse, idUser);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(
            summary = "Delete a course"
    )
    @DeleteMapping("/delete-course/{idCourse}")
    public Long deleteCourse(@PathVariable("idCourse") Long idCourse) {

        courseService.deleteCourse(idCourse);

        return idCourse;
    }
}