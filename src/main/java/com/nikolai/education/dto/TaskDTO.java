package com.nikolai.education.dto;

import com.nikolai.education.enums.ProgressTaskEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskDTO {
    @NotNull
    private String name;
    @NotNull
    private String text;
    @NotNull
    private String description;
    private String dateCreated;
    private String dateStart;
    private String dateFinish;
    private ProgressTaskEnum progress;
    @NotNull
    private Integer expirationCountHours;


}
