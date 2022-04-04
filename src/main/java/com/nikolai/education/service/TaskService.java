package com.nikolai.education.service;

import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.enums.UserLogs;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Logs;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.repository.UserLogsRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final ConvertDto convertDto;
    private final TaskRepo taskRepo;
    private final CourseRepo courseRepo;
    private final UserLogsRepo userLogsRepo;
    private final UserRepo userRepo;

    public TaskDTO createTask(Task task, Long courseId, Integer expirationCountHours) {
        Optional<Course> course = courseRepo.findById(courseId);
        task.setCourse(course.get());
        task.setProgress(ProgressTask.NOT_START);
        task.setExpirationCountHours(expirationCountHours);
        log.info("create task for course {}", course.get().getName());
        taskRepo.save(task);

        User user = userRepo.getById(course.get().getCreatorId());
        Logs logs = new Logs(UserLogs.CREAT_TASK, user);
        userLogsRepo.save(logs);

        return convertDto.convertTask(task);
    }

    public void startTask(Long taskId) {
        Task task = taskRepo.getById(taskId);
        task.setProgress(ProgressTask.IN_PROGRESS);
        Logs logs = new Logs(UserLogs.STARTED_TASK, task.getUser());
        userLogsRepo.save(logs);
        taskRepo.save(task);
    }

    public void finishTask(Long taskId) {
        Task task = taskRepo.getById(taskId);
        task.setProgress(ProgressTask.DONE);
        taskRepo.save(task);
        Logs logs = new Logs(UserLogs.FINISH_TASK, task.getUser());
        userLogsRepo.save(logs);
    }
}
