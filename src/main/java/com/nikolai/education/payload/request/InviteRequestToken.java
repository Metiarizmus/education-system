package com.nikolai.education.payload.request;

import com.nikolai.education.enums.TypeRoles;
import com.nikolai.education.enums.TypeWayInvited;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class InviteRequestToken {

    private String email;
    private String telephoneNumber;
    @NotNull
    @NotBlank(message = "Please enter type invite(ADMIN, MANAGER, USER)")
    private TypeRoles role;
    private Integer expirationDateCount;
    private TypeWayInvited typeWayInvited;

}
