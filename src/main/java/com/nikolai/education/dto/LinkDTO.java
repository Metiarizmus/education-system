package com.nikolai.education.dto;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import lombok.Data;

@Data
public class LinkDTO {

    private Long id;
    private String dateCreated;
    private TypeRoles role;
    private TypeWayInvited wayInvited;

}
