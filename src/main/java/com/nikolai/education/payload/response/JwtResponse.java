package com.nikolai.education.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String token;
    private String refreshToken;

    private String type = "Bearer";
    private String email;
    private List<String> roles;
    @Value("${app.jwtExpirationMs}")
    private Long expiresAt;

    public JwtResponse(String token,String refreshToken, String email, List<String> roles) {
        this.token = token;
        this.email = email;
        this.roles = roles;
        this.refreshToken = refreshToken;
    }
}
