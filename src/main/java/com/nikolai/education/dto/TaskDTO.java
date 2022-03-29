package com.nikolai.education.dto;

import com.nikolai.education.enums.ProgressTask;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskDTO {
    @NotNull
    private String name;
    @NotNull
    private String content;
    @NotNull
    private String description;
    private String dateCreated;
    private String dateStart;
    private String dateFinish;
    private ProgressTask progress;
    @NotNull
    private Integer expirationCountHours;

}
