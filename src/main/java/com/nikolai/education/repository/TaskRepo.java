package com.nikolai.education.repository;


import com.nikolai.education.enums.ProgressTask;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface TaskRepo extends JpaRepository<Task, Long> {

    Set<Task> findAllByCourse(Course course);
    Task findByCourseAndNameContains(Course course, String testControl);
    List<Task> findTaskByProgress(ProgressTask progressTask);
}
