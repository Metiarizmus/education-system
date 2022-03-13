package com.nikolai.education.payload.request;

import com.nikolai.education.enums.StatusOrg;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrgRequest {

    @NotNull
    @NotBlank(message = "Please enter name organization")
    private String name;

    @NotNull
    @NotBlank(message = "Please enter description organization")
    private String description;

    @NotNull
    @NotBlank(message = "Please enter status organization(private or public)")
    private StatusOrg status;

}
