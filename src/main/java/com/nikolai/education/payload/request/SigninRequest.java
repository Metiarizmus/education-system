package com.nikolai.education.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class SigninRequest {

    @Email
    private String email;
    @NotNull
    private String password;

}
