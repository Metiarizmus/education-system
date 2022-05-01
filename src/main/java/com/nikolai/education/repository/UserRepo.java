package com.nikolai.education.repository;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);
    List<User> findAllByOrgAndRoles_nameRoles(Organization organization, TypeRolesEnum role);
    boolean existsByEmail(String email);
    User findByProgressTasks_task(Task task);
}
