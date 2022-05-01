package com.nikolai.education.util;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConvertDto {

    private final ModelMapper modelMapper;

    public OrgDTO convertOrg(Organization organization) {
        return modelMapper.map(organization, OrgDTO.class);
    }

    public UserDTO convertUser(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public CourseDTO convertCourse(Course course) {
        return modelMapper.map(course, CourseDTO.class);
    }

    public TaskDTO convertTask(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
