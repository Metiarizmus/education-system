package com.nikolai.education.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.enums.StatusOrg;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgDTO {

    private String name;
    private String description;
    private StatusOrg status;
    private Set<User> users;
    private Set<Course> courses;

}
