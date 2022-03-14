package com.nikolai.education.service;

import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.TaskRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final ConvertDto convertDto;
    private final TaskRepo taskRepo;
    private final CourseRepo courseRepo;

    public TaskDTO createTask(Task task, Long courseId) {
        Optional<Course> course = courseRepo.findById(courseId);
        task.setCourse(course.get());
        task.setProgress(ProgressTask.NOT_START);

        taskRepo.save(task);
        return convertDto.convertTask(task);
    }
}
