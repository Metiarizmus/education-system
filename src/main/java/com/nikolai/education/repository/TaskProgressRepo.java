package com.nikolai.education.repository;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.TaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskProgressRepo extends JpaRepository<TaskProgress, Long> {
    TaskProgress getByTaskId(Long id);

    List<TaskProgress> findByProgressTaskEnum(ProgressTaskEnum progressTaskEnum);
}
