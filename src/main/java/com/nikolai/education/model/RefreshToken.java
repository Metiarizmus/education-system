package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
@Data
public class RefreshToken {

    private Long id;
    @JsonIgnore
    private User user;
    private String token;
    private Long expiryDate;

}
