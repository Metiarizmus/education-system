package com.nikolai.education.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {

    private Long id;
    @JsonIgnore
    private User user;
    private String token;
    private Long expiryDate;

    public RefreshToken(Long id, User user, String token, Long expiryDate) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
