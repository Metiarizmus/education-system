package com.nikolai.education.repository;


import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepo extends JpaRepository<Task, Long> {

    List<Task> findAllByCourses(Course course);

    Task findByCoursesAndNameContains(Course course, String testControl);

    List<Task> findAllByProgressTasks_progressTaskEnum(ProgressTaskEnum progressTaskEnum);


//select tasks.name,tasks.description, tasks.text, tasks.id, progress_task_enum

    @Query(nativeQuery = true, value = "select * \n" +
            "from tasks \n" +
            "left join task_progress on tasks.id = task_progress.tasks_id \n" +
            "left join users on users.id=task_progress.users_id\n" +
            "left join task_courses on tasks.id=task_courses.tasks_id\n" +
            "left join courses on courses.id=task_courses.courses_id\n" +
            "where users.email =:email and courses.id =:id")
    List<Task> tasksForUser(@Param("email") String email, @Param("id") Long id);




}
