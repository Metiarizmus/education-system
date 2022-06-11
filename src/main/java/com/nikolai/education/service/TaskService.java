package com.nikolai.education.service;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.enums.UserLogsEnum;
import com.nikolai.education.exception.ResourceNotFoundException;
import com.nikolai.education.model.*;
import com.nikolai.education.repository.*;
import com.nikolai.education.util.WorkWithTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final CourseRepo courseRepo;
    private final UserLogsRepo userLogsRepo;
    private final UserRepo userRepo;
    private final TaskProgressRepo taskProgressRepo;
    private final TaskRepo taskRepo;
    private final WorkWithTime workWithTime;

    public Task createTask(Task task, Long courseId, Integer expirationCountHours) {
        Optional<Course> course = courseRepo.findById(courseId);
        if (course.isPresent()) {
            task.setExpirationCountHours(expirationCountHours);
            course.get().getTasks().add(task);


            for (User q : course.get().getUsers()) {
                if (q.getId() != course.get().getCreatorId()) {
                    taskProgressRepo.save(new TaskProgress(ProgressTaskEnum.NOT_START, task, q));
                }
            }

            courseRepo.save(course.get());

            User user = userRepo.getById(course.get().getCreatorId());
            Logs logs = new Logs(UserLogsEnum.CREAT_TASK, user);
            userLogsRepo.save(logs);

            log.info("create task for course {}", course.get().getName());
        } else throw new ResourceNotFoundException("Course", "id", courseId);
        return task;
    }

    @Transactional
    public Task startTask(Long taskId, String email) {

        User user = userRepo.findByEmail(email);

        Optional<Task> task = taskRepo.findById(taskId);

        task.get().setDateStart(workWithTime.dateNow());
        task.get().setDateFinish(workWithTime.dateFinish(task.get().getExpirationCountHours(), Calendar.HOUR_OF_DAY));
        taskRepo.save(task.get());

        TaskProgress taskProgress = taskProgressRepo.getByTaskIdAndUser(taskId, user);
        taskProgress.setProgressTaskEnum(ProgressTaskEnum.IN_PROGRESS);
        taskProgress.setTask(task.get());
        taskProgressRepo.save(taskProgress);

        Logs logs = new Logs(UserLogsEnum.STARTED_COURSE, user);
        userLogsRepo.save(logs);

        log.info("Course was started");

        return task.get();
    }

    @Transactional
    public void changeStatusTask(Long taskId, ProgressTaskEnum progressTaskEnum, String email) {

        User user = userRepo.findByEmail(email);

        Optional<Task> task = taskRepo.findById(taskId);

        if (task.get().getDateStart() == null) {

            task.get().setDateStart(workWithTime.dateNow());
            task.get().setDateFinish(workWithTime.dateFinish(task.get().getExpirationCountHours(), Calendar.HOUR_OF_DAY));
            taskRepo.save(task.get());

        }

        TaskProgress taskProgress = taskProgressRepo.getByTaskIdAndUser(taskId, user);

        if (taskProgress != null) {
            taskProgress.setProgressTaskEnum(progressTaskEnum);
            taskProgress.setTask(task.get());
            taskProgressRepo.save(taskProgress);

            Logs logs = new Logs(UserLogsEnum.CHANGE_STATUS_TASK, taskProgress.getUser());
            userLogsRepo.save(logs);
            log.info("task status was changed on {}", progressTaskEnum);

        } else throw new ResourceNotFoundException("Task", "id", taskId);
    }

    public List<Task> listTaskForUser(String email, Long idCourse) {

        User user = userRepo.findByEmail(email);

        List<Task> list = taskRepo.tasksForUser(email, idCourse);

        for (Task q : list) {
            List<TaskProgress> list1 = new ArrayList<>(q.getProgressTasks());
            for (TaskProgress qq : list1) {
                if (qq.getUser().getId() != user.getId()) {
                    q.getProgressTasks().remove(qq);
                }
            }
        }

        log.info("get tasks for user {}", email);
        return list;
    }

    @Transactional
    public void deleteTask(Long id, String email) {
        Optional<Task> task = taskRepo.findById(id);
        if (task.isPresent()) {

            Course course = courseRepo.findByTasks(task.get());
            course.getTasks().remove(task.get());
            taskRepo.delete(task.get());
            courseRepo.flush();


            User user = userRepo.findByEmail(email);
            Logs logs = new Logs(UserLogsEnum.DELETE_TASK, user);
            userLogsRepo.save(logs);
            log.info("delete task {}", task.get().getName());
        } else throw new ResourceNotFoundException("Task", "id", id);
    }

}
