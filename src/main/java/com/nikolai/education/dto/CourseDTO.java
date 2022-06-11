package com.nikolai.education.dto;

import com.nikolai.education.enums.ProgressTaskEnum;
import com.nikolai.education.model.Organization;
import lombok.Data;

import java.util.Set;

@Data
public class CourseDTO {
    private Long id;
    private String name;
    private String description;
    private String dateCreat;
    private String plan;
    private Set<TaskDTO> tasks;
    private Set<UserDTO> users;
    private Long creatorId;
    private Organization org;
    private String managerName;

    //private String dateExpirationDay;


    @Override
    public String toString() {
        return "CourseDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateCreat='" + dateCreat + '\'' +
                ", plan='" + plan + '\'' +
                '}';
    }
}
