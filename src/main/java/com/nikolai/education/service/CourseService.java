package com.nikolai.education.service;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.UserLogs;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.*;
import com.nikolai.education.redis.RedisService;
import com.nikolai.education.repository.*;
import com.nikolai.education.util.ConvertDto;
import com.nikolai.education.util.WorkWithTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserLogsRepo userLogsRepo;
    private final RedisService redisService;

    @Transactional
    public CourseDTO createCourse(Course course, Principal principal) {

        User user = userRepo.findByEmail(principal.getName());
        Organization organization = orgRepo.findByUsers(user);

        course.setOrg(organization);
        course.setUsers(Collections.singleton(user));
        course.setCreatorId(user.getId());
        courseRepo.save(course);

        Logs logs = new Logs(UserLogs.CREATE_COURSE, user);
        userLogsRepo.save(logs);

        log.info("create course from manager {}", principal.getName());
        return convertDto.convertCourse(course);
    }

    public List<? extends Object> getAllCourses(Principal principal, TypeRoles typeRoles) {
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

        String key = "list:courses";
        redisService.lPushAll(key, ArrayUtils.toArray(courseDTOS, CourseDTO.class));
        List<Object> cachedLogs = redisService.lRange(key, 0, list.size());
        if (cachedLogs.isEmpty()) {
            return courseDTOS;
        }
        return cachedLogs;
    }

    @Cacheable(value= "Course", key="#id")
    public CourseDTO getCourseById(Long id) {
        Optional<Course> course = courseRepo.findById(id);
        Set<Task> tasks = taskRepo.findAllByCourse(course.get());
        course.get().setTasks(tasks);
        log.info("get course by id {}", id);
        return convertDto.convertCourse(course.get());
    }

    @Transactional
    public TaskDTO startCourse(Long id, Principal principal) {

        Optional<Course> course = courseRepo.findById(id);
        Task controlTask = taskRepo.findByCourseAndNameContains(course.get(), "Test Control");
        User user = userRepo.findByEmail(principal.getName());
        controlTask.setUser(user);
        controlTask.setDateStart(workWithTime.dateNow());
        controlTask.setDateFinish(workWithTime.dateFinish(controlTask.getExpirationCountHours(), Calendar.HOUR_OF_DAY));
        controlTask.setProgress(ProgressTask.IN_PROGRESS);

        taskRepo.save(controlTask);
        Logs logs = new Logs(UserLogs.STARTED_COURSE, user);
        userLogsRepo.save(logs);
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

        Logs logs = new Logs(UserLogs.ACCEPTED_COURSE, user);
        userLogsRepo.save(logs);

        sendMessages.sendNotificationAccepted(sender.getEmail(), mailContent, emailSubject);
        return course.get();
    }

    @Transactional
    public void deleteCourse(Long idCourse) {
        Optional<Course> course = courseRepo.findById(idCourse);
        courseRepo.delete(course.get());
        Optional<User> user = userRepo.findById(course.get().getCreatorId());
        Logs logs = new Logs(UserLogs.DELETE_COURSE, user.get());
        userLogsRepo.save(logs);
        log.info("delete the course {}", course.get().getName());
    }

}
