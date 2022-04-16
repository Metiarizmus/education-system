package com.nikolai.education.dto;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import lombok.Data;

@Data
public class LinkDTO {

    private Long id;
    private String dateCreated;
    private TypeRolesEnum role;
    private TypeWayInvitedEnum wayInvited;

}
