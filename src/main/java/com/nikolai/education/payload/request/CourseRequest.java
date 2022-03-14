package com.nikolai.education.payload.request;

import lombok.Data;

@Data
public class CourseRequest {

    private String name;
    private String description;
    private String plan;

}
