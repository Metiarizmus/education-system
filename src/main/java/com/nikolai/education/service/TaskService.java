package com.nikolai.education.service;

import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final ConvertDto convertDto;
    private final TaskRepo taskRepo;
    private final CourseRepo courseRepo;

    public TaskDTO createTask(Task task, Long courseId, Integer expirationCountHours) {
        Optional<Course> course = courseRepo.findById(courseId);
        task.setCourse(course.get());
        task.setProgress(ProgressTask.NOT_START);
        task.setExpirationCountHours(expirationCountHours);
        log.info("create task for course {}", course.get().getName());
        taskRepo.save(task);
        return convertDto.convertTask(task);
    }

    public List<TaskDTO> getAllTasksInCourse(Long idCourse) {
        Optional<Course> course = courseRepo.findById(idCourse);
        Set<Task> tasks = taskRepo.findAllByCourse(course.get());
        log.info("get all tasks for course {}", course.get().getName());
        return tasks.stream().map(convertDto::convertTask).collect(Collectors.toList());
    }

    public void startTask(Long taskId) {
        Task task = taskRepo.getById(taskId);
        task.setProgress(ProgressTask.IN_PROGRESS);
        taskRepo.save(task);
    }

    public void finishTask(Long taskId) {
        Task task = taskRepo.getById(taskId);
        task.setProgress(ProgressTask.DONE);
        taskRepo.save(task);
    }
}
