package com.hideyoshi.backendportfolio.base.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthDTO implements Serializable {

    private Long id;

    private String name;

    private String email;

    private String username;

    private String profilePictureUrl;

    private List<Role> roles;

    private Provider provider;

    private TokenDTO accessToken;

    private TokenDTO refreshToken;

    public AuthDTO(UserDTO user, TokenDTO accessToken, TokenDTO refreshToken) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.roles = user.getRoles();
        this.provider = user.getProvider();
        this.profilePictureUrl = user.getProfilePictureUrl();

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
