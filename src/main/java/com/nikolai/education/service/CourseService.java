package com.nikolai.education.service;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import com.nikolai.education.util.WorkWithTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final CourseRepo courseRepo;
    private final ConvertDto convertDto;
    private final TaskRepo taskRepo;
    private final SendMessages sendMessages;
    private final WorkWithTime workWithTime;


    public CourseDTO createCourse(Course course, Principal principal) {

        User user = userRepo.findByEmail(principal.getName());
        Organization organization = orgRepo.findByUsers(user);

        course.setOrg(organization);
        course.setUsers(Collections.singleton(user));
        course.setCreatorId(user.getId());
        courseRepo.save(course);

        log.info("create course from manager {}", principal.getName());
        return convertDto.convertCourse(course);
    }

    public List<CourseDTO> getAllCourses(Principal principal, TypeRoles typeRoles) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = null;
        if (TypeRoles.ROLE_MANAGER.equals(typeRoles)) {
            list = courseRepo.findAllByCreatorId(user.getId());
        }
        if (TypeRoles.ROLE_ADMIN.equals(typeRoles)) {
            Organization organization = orgRepo.findByUsers(user);
            list = courseRepo.findAllByOrg(organization);
        }
        if (TypeRoles.ROLE_USER.equals(typeRoles)) {
            list = courseRepo.findAllByUsers(user);
        }

        assert list != null;
        List<CourseDTO> courseDTOS = list.stream().map(convertDto::convertCourse).collect(Collectors.toList());
        log.info("get all courses for {} {}", typeRoles, principal.getName());
        return courseDTOS;
    }

    public CourseDTO getCourseById(Long id) {
        Optional<Course> course = courseRepo.findById(id);
        Set<Task> tasks = taskRepo.findAllByCourse(course.get());
        course.get().setTasks(tasks);
        log.info("get course by id {}", id);
        return convertDto.convertCourse(course.get());
    }

    public TaskDTO startCourse(Long id, Principal principal) {

        Optional<Course> course = courseRepo.findById(id);
        Task controlTask = taskRepo.findByCourseAndNameContains(course.get(), "Test Control");
        User user = userRepo.findByEmail(principal.getName());
        controlTask.setUser(user);
        controlTask.setDateStart(workWithTime.dateNow());

        controlTask.setDateFinish(workWithTime.dateFinish(controlTask.getExpirationCountHours(), Calendar.HOUR_OF_DAY));

        controlTask.setProgress(ProgressTask.IN_PROGRESS);

        taskRepo.save(controlTask);
        log.info("user {} start course with {}", principal.getName(), course.get().getName());
        return convertDto.convertTask(controlTask);
    }

    public Course acceptedCourse(String emailUser, Long idCourse, Long senderId) {
        User sender = userRepo.getById(senderId);
        User user = userRepo.findByEmail(emailUser);
        Optional<Course> course = courseRepo.findById(idCourse);
        course.get().getUsers().add(user);
        courseRepo.save(course.get());

        String mailContent = String.format("User %s accepted invitation to the course \"%s\"", emailUser, course.get().getName());
        String emailSubject = "accepted the invitation";

        sendMessages.sendNotificationAccepted(sender.getEmail(), mailContent, emailSubject);

        return course.get();
    }

}
