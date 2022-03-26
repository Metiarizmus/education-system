package com.nikolai.education.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.Organization;
import com.nikolai.education.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dateRegistr;
    private Set<Role> roles;
//    @JsonIgnore
//    private Set<Organization> org;
//    @JsonIgnore
//    private Set<Course> courses;


}
