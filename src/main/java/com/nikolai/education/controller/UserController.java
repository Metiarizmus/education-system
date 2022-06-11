package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.service.TaskService;
import com.nikolai.education.util.ConvertDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
@Tag(name = "User controller", description = "points for using courses and create organizations")
public class UserController {

    private final OrgService orgService;
    private final CourseService courseService;
    private final SendMessages sendMessages;
    private final ConfirmTokenRepo tokenRepo;
    private final TaskService taskService;
    private final UserRepo userRepo;
    private final ConvertDto convertDto;


    @Operation(
            summary = "get user"
    )
    @GetMapping("/{email}")
    public UserDTO getCurrentUser(@PathVariable String email) {

        return convertDto.convertUser(userRepo.findByEmail(email));
    }

    @Operation(
            summary = "Get list of courses for user"
    )
    @GetMapping("/courses")
    public List<CourseDTO> getCourses(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = courseService.getAllCourses(user, TypeRolesEnum.ROLE_USER);
        List<CourseDTO> courseDTOS = list.stream().map(convertDto::convertCourse).collect(Collectors.toList());
        return courseDTOS;
    }

    @Operation(
            summary = "Get course by id"
    )
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return new ResponseEntity<>(convertDto.convertCourse(courseService.getCourseById(id)), HttpStatus.OK);
    }

    @Operation(
            summary = "Accept an invitation to the course"
    )
    @GetMapping("/accept-course")
    public ResponseEntity<?> acceptCourse(@RequestParam("confirmToken") String confToken) {

        InvitationLink token = tokenRepo.findByConfirmationToken(confToken);

        if (!sendMessages.validLink(token.getFinishDate())) {
            return ResponseEntity.badRequest().body("Error: Link not valid!");
        }

        Course course = courseService.acceptedCourse(token.getUser().getEmail(), token.getIdCourse(), token.getIdSender());

        log.info("accept course and send notification about it to the sender");
        return new ResponseEntity<>(convertDto.convertCourse(course), HttpStatus.ACCEPTED);
    }

    @GetMapping("/change-status-task/{id}")
    public ResponseEntity<?> changeStatusTask(@PathVariable Long id, @RequestParam("status") String progressTaskEnum,
                                              Principal principal) {


        taskService.changeStatusTask(id, ProgressTaskEnum.valueOf(progressTaskEnum), principal.getName());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/tasks/{id}")
    public List<TaskDTO> getTasks(Principal principal,
                                  @PathVariable Long id) {

        List<Task> list = taskService.listTaskForUser(principal.getName(), id);
        List<TaskDTO> tasksDTOS = list.stream().map(convertDto::convertTask).collect(Collectors.toList());
        return tasksDTOS;
    }

    @Operation(
            summary = "Join a public organization"
    )
    @GetMapping("/join-public-orgs/{id}")
    public ResponseEntity<HttpStatus> joinPublicOrgById(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        orgService.joinInPublicOrg(id, user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
