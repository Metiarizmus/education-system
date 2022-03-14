package com.nikolai.education.dto;

import lombok.Data;

@Data
public class TaskDTO {
    private String name;
    private String text;
    private String description;
    private String dateCreated;
    private String dateStart;
    private String dateFinish;
}
