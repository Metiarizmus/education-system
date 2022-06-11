package com.nikolai.education.service;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.mail.SendMessages;
import com.nikolai.education.model.*;
import com.nikolai.education.repository.*;
import com.nikolai.education.util.WorkWithTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final CourseRepo courseRepo;
    private final TaskRepo taskRepo;
    private final SendMessages sendMessages;
    private final WorkWithTime workWithTime;
    private final UserLogsRepo userLogsRepo;
    private final CacheManagerService<Course> cacheManagerService;
    private final TaskProgressRepo taskProgressRepo;

    @Transactional
    public Course createCourse(Course course, User user, Long selectedOrg) {

        Optional<Organization> organization = orgRepo.findById(selectedOrg);
        course.setOrg(organization.get());

        course.setUsers(Collections.singleton(user));
        course.setCreatorId(user.getId());
        course.setDateCreat(workWithTime.dateNow());
        courseRepo.save(course);

        Logs logs = new Logs(UserLogsEnum.CREATE_COURSE, user);
        userLogsRepo.save(logs);

        log.info("create course from manager {}", user.getEmail());
        return course;
    }

    public Course updateCourse(Course course) {
        log.info("update course {}", course.getName());
        return courseRepo.save(course);
    }

    public List<Course> getAllCourses(User user, TypeRolesEnum typeRoles) {
        // String key = "list:courses";

        List<Course> list = null;
        if (TypeRolesEnum.ROLE_MANAGER.equals(typeRoles)) {
            list = courseRepo.findAllByCreatorId(user.getId());
        }
        if (TypeRolesEnum.ROLE_ADMIN.equals(typeRoles)) {
            Organization organization = orgRepo.findByUsers(user);
            list = courseRepo.findAllByOrg(organization);
        }
        if (TypeRolesEnum.ROLE_USER.equals(typeRoles)) {
            list = courseRepo.findAllByUsers(user);
        }

        log.info("get all courses for {} {}", typeRoles, user.getEmail());

        return list;
        //    return cacheManagerService.cachedList(key, list);
    }

//    @Cacheable(value = "Course", key = "#id")
    public Course getCourseById(Long id) {

        return courseRepo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Course", "id", id)
        );
    }

    @Transactional
    public Task startCourse(Long id, User user) {

        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            Task controlTask = taskRepo.findByCoursesAndNameContains(course.get(), "Test Control");

            if (controlTask == null) {
                List<Task> task = taskRepo.findAllByCourses(course.get());
                controlTask = task.get(0);
            }

            controlTask.setDateStart(workWithTime.dateNow());
            controlTask.setDateFinish(workWithTime.dateFinish(controlTask.getExpirationCountHours(), Calendar.HOUR_OF_DAY));
            taskRepo.save(controlTask);

            Logs logs = new Logs(UserLogsEnum.STARTED_COURSE, user);
            userLogsRepo.save(logs);

            TaskProgress taskProgress = new TaskProgress(ProgressTaskEnum.IN_PROGRESS, controlTask, user);
            taskProgressRepo.save(taskProgress);

            log.info("user {} start course with {}", user.getEmail(), course.get().getName());
            return controlTask;
        } else throw new ResourceNotFoundException("Course", "id", id);

    }

    @Transactional
    public Course acceptedCourse(String emailUser, Long idCourse, Long senderId) {
        User sender = userRepo.getById(senderId);
        User user = userRepo.findByEmail(emailUser);
        Optional<Course> course = courseRepo.findById(idCourse);
        if (course.isPresent()) {

            course.get().getUsers().add(user);
            courseRepo.save(course.get());

            String mailContent = String.format("User %s accepted invitation to the course \"%s\"", emailUser, course.get().getName());
            String emailSubject = "accepted the invitation";

            Logs logs = new Logs(UserLogsEnum.ACCEPTED_COURSE, user);
            userLogsRepo.save(logs);

            sendMessages.sendNotificationAccepted(sender.getEmail(), mailContent, emailSubject);
            log.info("accepted user {} to the course {}", user.getEmail(), course.get().getName());
            return course.get();
        } else throw new ResourceNotFoundException("Course", "id", idCourse);

    }

    @Transactional
    public void deleteCourse(Long idCourse) {
        Optional<Course> course = courseRepo.findById(idCourse);

        if (course.isPresent()) {
            User user = userRepo.findById(course.get().getCreatorId()).orElseThrow();
            courseRepo.delete(course.get());
            Logs logs = new Logs(UserLogsEnum.DELETE_COURSE, user);
            userLogsRepo.save(logs);
            log.info("delete the course {}", course.get().getName());
        } else throw new ResourceNotFoundException("Course", "id", idCourse);

    }

}
