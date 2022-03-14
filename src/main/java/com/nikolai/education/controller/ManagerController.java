package com.nikolai.education.controller;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.payload.request.CourseRequest;
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
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final CourseService courseService;
    private final TaskService taskService;

    @PostMapping("/createCourse")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest courseRequest, Principal principal) {

        Course course = new Course(courseRequest.getName(), courseRequest.getDescription(), courseRequest.getPlan());
        CourseDTO courseDTO = courseService.createCourse(course, principal);

        return new ResponseEntity<>(courseDTO, HttpStatus.OK);
    }

    @GetMapping("/getMyCourses")
    public ResponseEntity<List<CourseDTO>> listCourses(Principal principal) {

        return new ResponseEntity<>(courseService.getAllMyCourses(principal), HttpStatus.OK);
    }

    @GetMapping("/getMyCoursesById")
    public ResponseEntity<CourseDTO> coursesById(@RequestParam("id") Long id) {

        return new ResponseEntity<>(courseService.getCourseById(id), HttpStatus.OK);
    }

    //test
    @PostMapping("/createTask")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, @RequestParam("id") Long idCourse) {

        Task task = new Task(taskRequest.getName(), taskRequest.getText(), taskRequest.getDescription());
        TaskDTO taskDTO = taskService.createTask(task, idCourse);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }
}
