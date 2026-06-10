package com.yixin.patrol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private UserDTO user;

    public LoginResponse(String token, UserDTO user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }
}
