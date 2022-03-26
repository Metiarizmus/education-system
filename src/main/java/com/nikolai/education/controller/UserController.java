package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.ConfirmationToken;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.payload.request.OrgRequest;
import com.nikolai.education.repository.ConfirmTokenRepo;
import com.nikolai.education.service.CourseService;
import com.nikolai.education.service.OrgService;
import com.nikolai.education.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final OrgService orgService;
    private final CourseService courseService;
    private final SendMessages sendMessages;
    private final ConfirmTokenRepo tokenRepo;
    private final TaskService taskService;

    @PostMapping("/createOrg")
    public ResponseEntity<?> createOrg(@Valid @RequestBody OrgRequest orgRequest, Principal principal) {
        Organization organization = new Organization(orgRequest.getName(), orgRequest.getDescription(), orgRequest.getStatus());

        OrgDTO org = orgService.createOrg(organization, principal);

        return new ResponseEntity<>(org, HttpStatus.OK);
    }

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getCourses(Principal principal) {
        return new ResponseEntity<>(courseService.getAllCourses(principal, TypeRoles.ROLE_USER), HttpStatus.OK);
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable Long id) {
        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.OK);
    }


    @GetMapping("/accept-course")
    public ResponseEntity<?> acceptCourse(@RequestParam("confirmToken") String confToken) {

        ConfirmationToken token = tokenRepo.findByConfirmationToken(confToken);

        if (!sendMessages.validLink(token.getFinishDate())) {
            return ResponseEntity.badRequest().body("Error: Link not valid!");
        }

        Course course = courseService.acceptedCourse(token.getUser().getEmail(), token.getIdCourse(), token.getIdSender());

        log.info("accept course and send notification about it to sender");
        return new ResponseEntity<>("course \"" + course.getName() + "\" was accepted to you", HttpStatus.ACCEPTED);
    }

    @GetMapping("/start-course/{id}")
    public ResponseEntity<TaskDTO> startCourse(@PathVariable Long id, Principal principal) {
        TaskDTO taskDTO = courseService.startCourse(id, principal);

        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    @GetMapping("start-tasks/{id}")
    public ResponseEntity<?> startTask(@PathVariable Long id) {
        taskService.startTask(id);
        return new ResponseEntity<>("You have started the task", HttpStatus.OK);
    }

    @GetMapping("finish-tasks/{id}")
    public ResponseEntity<?> finishTask(@PathVariable Long id) {
        taskService.finishTask(id);
        return new ResponseEntity<>("You have started the task", HttpStatus.OK);
    }

    @GetMapping("/find-public-org")
    public List<OrgDTO> findAllPublicOrgs() {
        return orgService.findAllPublicOrg();
    }

    @GetMapping("/find-public-org/{id}")
    public OrgDTO findPublicOrgById(@PathVariable Long id) {
        return orgService.findOrgById(id);
    }

    @GetMapping("/join-public-org/{id}")
    public ResponseEntity<?> joinPublicOrgById(@PathVariable Long id, Principal principal) {
        orgService.joinInPublicOrg(id, principal);

        return new ResponseEntity<>("you join to the organization", HttpStatus.ACCEPTED);
    }

}
