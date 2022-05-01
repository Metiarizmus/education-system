package com.nikolai.education.repository;


import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findAllByCourses(Course course);
    Task findByCoursesAndNameContains(Course course, String testControl);
    List<Task> findAllByProgressTasks_progressTaskEnum(ProgressTaskEnum progressTaskEnum);
}
