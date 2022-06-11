package com.nikolai.education.repository;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.TaskProgress;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskProgressRepo extends JpaRepository<TaskProgress, Long> {
    TaskProgress getByTaskIdAndUser(Long id, User user);

}
