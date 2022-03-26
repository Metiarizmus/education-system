package com.nikolai.education.payload.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TaskRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String content;
    @NotNull
    private Integer expirationCountHours;
}
