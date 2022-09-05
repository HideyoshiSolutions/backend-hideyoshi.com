package com.hideyoshi.backendportfolio.base.user.service;

import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.Valid;
import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO saveUser(@Valid UserDTO user);

    void alterUser(Long id, @Valid UserDTO user);

    void deleteUser(Long id);

    void addRoleToUser(Long id, String roleName);

    void removeRoleFromUser(Long id, String roleName);

    UserDTO getUser(Long id);

    UserDTO getUser(String username);

    List<UserDTO> getUsers();
}
