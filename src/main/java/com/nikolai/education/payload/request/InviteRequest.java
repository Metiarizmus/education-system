package com.nikolai.education.payload.request;

import com.nikolai.education.enums.TypeRolesEnum;
import com.nikolai.education.enums.TypeWayInvitedEnum;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InviteRequest {

    private String email;
    private String telephoneNumber;
    @NotNull
    @NotBlank(message = "Please enter type invite(ADMIN, MANAGER, USER)")
    private TypeRolesEnum role;
    private Integer expirationDateCount;
    private TypeWayInvitedEnum typeWayInvited;
    private String botToken;
    private Integer chatId;

    public InviteRequest(String email, String telephoneNumber, TypeRolesEnum role, Integer expirationDateCount, TypeWayInvitedEnum typeWayInvited) {
        this.email = email;
        this.telephoneNumber = telephoneNumber;
        this.role = role;
        this.expirationDateCount = expirationDateCount;
        this.typeWayInvited = typeWayInvited;
    }
}
