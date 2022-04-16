package com.nikolai.education.repository;

import com.nikolai.education.model.TaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskProgressRepo extends JpaRepository<TaskProgress, Long> {
    TaskProgress getByTaskId(Long id);
}
