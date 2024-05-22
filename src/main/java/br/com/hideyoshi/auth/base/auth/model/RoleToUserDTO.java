package br.com.hideyoshi.auth.base.auth.model;

import lombok.Data;

@Data
public
class RoleToUserDTO {
    private String username;
    private String roleName;
}
