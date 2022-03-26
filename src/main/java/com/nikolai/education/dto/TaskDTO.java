package com.nikolai.education.dto;

import com.nikolai.education.enums.ProgressTask;
import lombok.Data;

@Data
public class TaskDTO {
    private String name;
    private String text;
    private String description;
    private String dateCreated;
    private String dateStart;
    private String dateFinish;
    private ProgressTask progress;
    private Integer expirationCountHours;

}
