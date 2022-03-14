package com.nikolai.education.repository;

import com.nikolai.education.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Long> {

    List<Course> findAllByCreatorId(Long id);
}
