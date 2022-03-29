package com.nikolai.education.dto;

import com.nikolai.education.enums.StatusOrg;
import com.nikolai.education.model.Course;
import com.nikolai.education.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgDTO {

    @NotNull
    @NotBlank(message = "Please enter name organization")
    private String name;
    @NotNull
    @NotBlank(message = "Please enter description organization")
    private String description;
    @NotNull
    @NotBlank(message = "Please enter status organization(private or public)")
    private StatusOrg status;
    private Set<User> users;
    private Set<Course> courses;

}
