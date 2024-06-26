package br.com.hideyoshi.auth.model;

import br.com.hideyoshi.auth.enums.Provider;
import br.com.hideyoshi.auth.enums.Role;
import br.com.hideyoshi.auth.entity.User;
import br.com.hideyoshi.auth.util.validator.email.unique.UniqueEmail;
import br.com.hideyoshi.auth.util.validator.email.valid.ValidEmail;
import br.com.hideyoshi.auth.util.validator.password.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private String name;

    @NotEmpty
    @ValidEmail
    @UniqueEmail
    private String email;

    @NotEmpty
    private String username;

    @Nullable
    @ValidPassword
    private String password;

    @Size(min = 1)
    private List<Role> roles;

    private String profilePictureUrl;

    private Provider provider;

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.provider = Provider.byValue(entity.getProvider());
        this.roles = entity.getRoles();
    }

    public User toEntity() {
        return new User(
                this.id,
                this.name,
                this.email,
                this.username,
                this.password,
                this.provider.getName(),
                Objects.nonNull(this.roles) ? this.roles.stream()
                        .map(role -> role.getDescription())
                        .collect(Collectors.joining("&")) : Role.USER.getDescription()
        );
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public UserDTO toResponse() {
        return UserDTO.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .username(this.username)
                .provider(this.provider)
                .profilePictureUrl(this.profilePictureUrl)
                .build();
    }

}
