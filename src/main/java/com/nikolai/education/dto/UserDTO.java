package com.nikolai.education.dto;

import com.nikolai.education.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    @NotNull
    @NotBlank(message="Please enter your first name")
    @Size(min = 3)
    private String firstName;

    @NotNull
    @NotBlank(message="Please enter your last name")
    @Size(min = 3)
    private String lastName;

    @Email
    @NotBlank(message="Please enter your valid email")
    private String email;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "please enter your correct phone number")
    private String phoneNumber;

    @NotNull
    @NotBlank(message="Please enter your password")
    @Size(min = 4)
    private String password;


    private String dateRegistr;
    private Set<Role> roles;


}
