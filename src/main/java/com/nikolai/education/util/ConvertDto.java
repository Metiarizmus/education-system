package com.nikolai.education.util;

import com.nikolai.education.dto.CourseDTO;
import com.nikolai.education.dto.OrgDTO;
import com.nikolai.education.dto.TaskDTO;
import com.nikolai.education.dto.UserDTO;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Task;
import com.nikolai.education.model.User;
import com.nikolai.education.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConvertDto {

    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    public OrgDTO convertOrg(Organization organization) {
        return modelMapper.map(organization, OrgDTO.class);
    }

    public UserDTO convertUser(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public CourseDTO convertCourse(Course course) {
        CourseDTO c = modelMapper.map(course, CourseDTO.class);
        if (course.getId() != null) {
            User u = userRepo.getById(c.getCreatorId());
            c.setManagerName(u.getFirstName() + " " + u.getLastName());
        }
        return c;
    }

    public TaskDTO convertTask(Task task) {
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
        return taskDTO;
    }
}
