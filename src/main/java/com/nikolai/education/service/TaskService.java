package com.nikolai.education.service;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.model.*;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.TaskProgressRepo;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final CourseRepo courseRepo;
    private final UserLogsRepo userLogsRepo;
    private final UserRepo userRepo;
    private final TaskProgressRepo taskProgressRepo;

    @Transactional
    public Task createTask(Task task, Long courseId, Integer expirationCountHours) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent()) {
            task.setExpirationCountHours(expirationCountHours);
            course.get().setTasks(Collections.singleton(task));
            log.info("create task for course {}", course.get().getName());
            courseRepo.save(course.get());
            User user = userRepo.getById(course.get().getCreatorId());
            Logs logs = new Logs(UserLogsEnum.CREAT_TASK, user);
            userLogsRepo.save(logs);
        } else throw new ResourceNotFoundException("Course", "id", courseId);
        return task;
    }

    @Transactional
    public void changeStatusTask(Long taskId, ProgressTaskEnum progressTaskEnum) {
        TaskProgress taskProgress = taskProgressRepo.getByTaskId(taskId);
        if (taskProgress != null) {
            taskProgress.setProgressTaskEnum(progressTaskEnum);
            Logs logs = new Logs(UserLogsEnum.CHANGE_STATUS_TASK, taskProgress.getUser());
            userLogsRepo.save(logs);
            taskProgressRepo.save(taskProgress);
            log.info("Finish task with id {}", taskId);
        }  else throw new ResourceNotFoundException("Task", "id", taskId);

    }
}
