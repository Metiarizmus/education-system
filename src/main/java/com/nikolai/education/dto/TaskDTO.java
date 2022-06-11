package com.nikolai.education.dto;

import com.nikolai.education.model.TaskProgress;
import lombok.Data;

import java.util.Set;

@Data
public class TaskDTO {
    private Long id;
    private String name;
    private String text;
    private String description;
    private String dateCreated;
    private String dateStart;
    private String dateFinish;
    private Integer expirationCountHours;
    private Set<TaskProgress> progressTasks;


}
