package com.nikolai.education.dto;

import com.nikolai.education.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dateRegistr;
    private Set<Role> roles;
}
