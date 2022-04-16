package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.InvitationLink;
import com.nikolai.education.model.Organization;
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

import javax.validation.Valid;
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
            summary = "Create a organization"
    )
    @PostMapping("/createOrg")
    public ResponseEntity<?> createOrg(@Valid @RequestBody OrgDTO orgRequest, Principal principal) {
        Organization organization = new Organization(orgRequest.getName(), orgRequest.getDescription(), orgRequest.getStatus());
        User user = userRepo.findByEmail(principal.getName());
        orgService.createOrg(organization, user);
        return new ResponseEntity<>("Organization was created successfully", HttpStatus.OK);

    }

    @Operation(
            summary = "Get list of courses for user"
    )
    @GetMapping("/courses")
    public ResponseEntity<List<?>> getCourses(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = courseService.getAllCourses(user, TypeRolesEnum.ROLE_USER);
        List<CourseDTO> courseDTOS = list.stream().map(convertDto::convertCourse).collect(Collectors.toList());
        return new ResponseEntity<>(courseDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Get course by id"
    )
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
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

        log.info("accept course and send notification about it to sender");
        return new ResponseEntity<>("course \"" + course.getName() + "\" was accepted to you", HttpStatus.ACCEPTED);
    }

    @GetMapping("/start-courses/{id}")
    public ResponseEntity<?> startCourse(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        return new ResponseEntity<>(convertDto.convertTask(courseService.startCourse(id, user)), HttpStatus.OK);
    }


    @GetMapping("/change-status-task/{id}")
    public ResponseEntity<?> changeStatusTask(@PathVariable Long id, @RequestParam("status") ProgressTaskEnum progressTaskEnum) {
        taskService.changeStatusTask(id, progressTaskEnum);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(
            summary = "Get all public organizations in the system"
    )
    @GetMapping("/public-orgs")
    public ResponseEntity<?> findAllPublicOrgs() {
        List<Organization> list = orgService.getAllPublicOrg();

        List<OrgDTO> orgDTOS = list.stream().map(convertDto::convertOrg).collect(Collectors.toList());

        if (list.isEmpty()) {
            return new ResponseEntity<>("Not have public organizations", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orgDTOS, HttpStatus.OK);

    }

    @GetMapping("/public-orgs/{id}")
    public OrgDTO findPublicOrgById(@PathVariable Long id) {
        return convertDto.convertOrg(orgService.getOrgById(id));
    }

    @Operation(
            summary = "Join a public organization"
    )
    @GetMapping("/join-public-orgs/{id}")
    public ResponseEntity<?> joinPublicOrgById(@PathVariable Long id, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        orgService.joinInPublicOrg(id, user);

        return new ResponseEntity<>("you join to the organization", HttpStatus.OK);
    }

}
