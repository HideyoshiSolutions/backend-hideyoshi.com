package com.hideyoshi.backendportfolio.base.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.entity.User;
import com.hideyoshi.backendportfolio.util.validator.email.unique.UniqueEmail;
import com.hideyoshi.backendportfolio.util.validator.email.valid.ValidEmail;
import com.hideyoshi.backendportfolio.util.validator.password.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements UserDetails {

    private Long id;

    @NotEmpty
    private String fullname;

    @NotEmpty
    @ValidEmail
    @UniqueEmail
    private String email;

    @NotEmpty
    private String username;

    @Nullable
    @ValidPassword
    private String password;

    @Size(min=1)
    private List<Role> roles;

    private TokenDTO accessToken;

    private TokenDTO refreshToken;

    private Provider provider;

    public UserDTO(
            String fullname,
            String email,
            String username,
            String password
    ) {
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = List.of(Role.USER);
    }

    public UserDTO(
            String fullname,
            String email,
            String username,
            String password,
            List<Role> roles
    ) {
        this.fullname = fullname;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.fullname = entity.getFullname();
        this.email = entity.getEmail();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.roles = entity.getRoles();
    }

    public User toEntity() {
        return new User(
                this.id,
                this.fullname,
                this.email,
                this.username,
                this.password,
                Objects.nonNull(this.roles) ? this.roles.stream()
                        .map(role -> role.getDescription())
                        .collect(Collectors.joining("&")) : Role.USER.getDescription()
        );
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getDescription()))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserDTO toResponse() {
        return UserDTO.builder()
                .fullname(this.fullname)
                .email(this.email)
                .username(this.username)
                .build();
    }

    public UserDTO toResponse(TokenDTO accessToken, TokenDTO refreshToken) {
        return UserDTO.builder()
                .id(this.id)
                .fullname(this.fullname)
                .email(this.email)
                .username(this.username)
                .roles(this.roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
