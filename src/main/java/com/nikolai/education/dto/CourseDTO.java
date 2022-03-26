package com.nikolai.education.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CourseDTO {
    private String name;
    private String description;
    private String dateCreat;
    private String plan;
    private Set<TaskDTO> tasks;
    private String dateExpirationDay;

}
