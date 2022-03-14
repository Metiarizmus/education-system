package com.nikolai.education.service;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.CourseRepo;
import com.nikolai.education.repository.OrgRepo;
import com.nikolai.education.repository.UserRepo;
import com.nikolai.education.util.ConvertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final CourseRepo courseRepo;
    private final ConvertDto convertDto;

    public CourseDTO createCourse(Course course, Principal principal) {

        User user = userRepo.findByEmail(principal.getName());
        Organization organization = orgRepo.findByUsers(user);

        course.setOrg(organization);
        course.setUsers(Collections.singleton(user));
        course.setCreatorId(user.getId());
        courseRepo.save(course);

        log.info("create course from manager {}", principal.getName());
        return convertDto.convertCourse(course);
    }

    public List<CourseDTO> getAllMyCourses(Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        List<Course> list = courseRepo.findAllByCreatorId(user.getId());
        List<CourseDTO> courseDTOS = list.stream().map(p -> convertDto.convertCourse(p)).collect(Collectors.toList());
        log.info("get all courses for manager {}", principal.getName());
        return courseDTOS;
    }

    public CourseDTO getCourseById(Long id) {

        Optional<Course> course = courseRepo.findById(id);

        log.info("get course by id {}", id);
        return convertDto.convertCourse(course.get());
    }
}
