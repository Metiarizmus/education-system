package com.nikolai.education.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nikolai.education.enums.StatusOrgEnum;
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
@JsonSerialize
public class OrgDTO {

    private Long id;
    @NotNull
    @NotBlank(message = "Please enter name organization")
    private String name;
    @NotNull
    @NotBlank(message = "Please enter description organization")
    private String description;
    @NotNull
    @NotBlank(message = "Please enter status organization(private or public)")
    private StatusOrgEnum status;
    private Set<UserDTO> users;
    private Set<CourseDTO> courses;
    private byte[] avatar;
    private String dateCreated;
    private Long creatorId;

}
