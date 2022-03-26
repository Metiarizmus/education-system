package com.nikolai.education.repository;

import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Long> {

    List<Course> findAllByCreatorId(Long id);
    List<Course> findAllByOrg(Organization organization);
    List<Course> findAllByUsers(User user);
}
