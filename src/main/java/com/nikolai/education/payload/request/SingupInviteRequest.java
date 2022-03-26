package com.nikolai.education.payload.request;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class SingupInviteRequest {
    @NotNull
    @NotBlank(message="Please enter your first name")
    @Size(min = 3)
    private String firstName;

    @NotNull
    @NotBlank(message="Please enter your last name")
    @Size(min = 3)
    private String lastName;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "please enter your correct phone number")
    private String phoneNumber;

    @NotNull
    @NotBlank(message="Please enter your password")
    @Size(min = 4)
    private String password;
}
