package com.nikolai.education.dto;

import com.nikolai.education.model.Task;
import lombok.Data;

import java.util.Set;

@Data
public class CourseDTO {
    private String name;
    private String description;
    private String dateCreat;
    private String plan;
    private Set<Task> tasks;
    private Set<UserDTO> users;

}
