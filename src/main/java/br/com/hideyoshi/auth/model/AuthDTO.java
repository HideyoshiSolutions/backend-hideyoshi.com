package br.com.hideyoshi.auth.model;

import br.com.hideyoshi.auth.entity.Provider;
import br.com.hideyoshi.auth.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
