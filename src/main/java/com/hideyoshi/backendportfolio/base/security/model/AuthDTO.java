package com.hideyoshi.backendportfolio.base.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDTO implements Serializable {

    private UserDTO user;

    private TokenDTO accessToken;

    private TokenDTO refreshToken;

    public AuthDTO(UserDTO user) {
        this.user = user.toResponse();
    }

    public AuthDTO(UserDTO user, TokenDTO accessToken, TokenDTO refreshToken) {
        this.user = user.toResponse();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
