package com.nikolai.education.dto;

import com.nikolai.education.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private byte[] avatar;
    private String phoneNumber;

    private String dateRegistr;
    private Set<Role> roles;

    public UserDTO(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatar=" + Arrays.toString(avatar) +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateRegistr='" + dateRegistr + '\'' +
                ", roles=" + roles +
                '}';
    }
}
