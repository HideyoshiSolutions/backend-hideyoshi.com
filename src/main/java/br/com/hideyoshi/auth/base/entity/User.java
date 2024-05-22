package br.com.hideyoshi.auth.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`", schema = "auth")
public class User {

    @Id
    @SequenceGenerator(name = "seq_user", sequenceName = "auth.user_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user")
    private Long id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "email",
            unique = true,
            nullable = false
    )
    private String email;


    @Column(
            name = "username",
            unique = true,
            nullable = false
    )
    private String username;


    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Column(
            name = "provider",
            nullable = false
    )
    private String provider;

    @Column(
            name = "roles",
            nullable = false
    )
    private String roles;

    public List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        if (Objects.nonNull(this.roles) && !this.roles.isEmpty()) {
            roles = stream(this.roles.split("&"))
                    .map(description -> Role.byValue(description))
                    .collect(Collectors.toList());
        }
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles.stream()
                .map(role -> role.getDescription())
                .collect(Collectors.joining("&"));
    }

}
