package com.hideyoshi.backendportfolio.base.user.service;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.entity.User;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.repo.UserRepository;
import com.hideyoshi.backendportfolio.util.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO saveUser(@Valid UserDTO user) {

        this.userRepo.findByUsername(user.getUsername()).ifPresent(userOnDB -> {
            throw new BadRequestException(String.format("User %s already exists. Try another UserName.", userOnDB.getUsername()));
        });

        user.setPassword(this.validatePassword(user));

        user.setRoles(this.validateRoles(user.getRoles()));

        log.info(String.format("Saving to the database user of name: %s", user.getName()));
        UserDTO userSaved = new UserDTO(userRepo.save(user.toEntity()));

        return userSaved;
    }

    @Override
    public void alterUser(Long id, @Valid UserDTO user) {

        this.userRepo.findById(id).ifPresentOrElse(userOnDB -> {
            User userToSave = user.toEntity();
            userToSave.setId(userOnDB.getId());
            userRepo.save(userToSave);
        }, () -> {
            throw new BadRequestException(String.format("User {} doesn't exist.", user.getUsername()));
        });
    }

    @Override
    public void deleteUser(Long id) {

        this.userRepo.findById(id).ifPresentOrElse(userOnDB -> {
            this.userRepo.delete(userOnDB);
        }, () -> {
            throw new BadRequestException("User doesn't exist.");
        });

    }

    @Override
    public void addRoleToUser(Long id, String roleName) {

        UserDTO userOnDB = this.getUser(id);
        Role newAuthority = Role.byValue(roleName);

        List<Role> roles = userOnDB.getRoles();
        if (Objects.nonNull(newAuthority) && !roles.contains(newAuthority)) {

            log.info(String.format("Adding to user %s the role %s",
                    userOnDB.getUsername(), newAuthority.getDescription()));

            roles.add(newAuthority);
            userOnDB.setRoles(roles);

            this.alterUser(userOnDB.getId(), userOnDB);

        }

    }

    @Override
    public void removeRoleFromUser(Long id, String roleName) {

        UserDTO userOnDB = this.getUser(id);
        Role toDeleteAuthority = Role.byValue(roleName);

        List<Role> roles = userOnDB.getRoles();
        if (!roles.isEmpty()) {

            log.info(String.format("Removing from user %s the role %s",
                    userOnDB.getUsername(), toDeleteAuthority.getDescription()));

            roles = roles.stream()
                    .filter(role -> !role.equals(toDeleteAuthority))
                    .collect(Collectors.toList());
            userOnDB.setRoles(roles);
            this.alterUser(userOnDB.getId(), userOnDB);
        }
    }

    @Override
    public UserDTO getUser(Long id) {
        log.info(String.format("Fetching user with id: %o", id));

        return new UserDTO(
                userRepo.findById(id)
                        .orElseThrow(() -> new BadRequestException("User Not Found. Please create an Account."))
        );
    }

    @Override
    public UserDTO getUser(String username) {
        log.info(String.format("Fetching user: %s", username));

        return new UserDTO(
                userRepo.findByUsername(username)
                        .orElseThrow(() -> new BadRequestException("User Not Found. Please create an Account."))
        );
    }

    @Override
    public List<UserDTO> getUsers() {
        log.info("Fetching all users.");

        return userRepo.findAll().stream()
                .map(user -> (new UserDTO(user)).toResponse())
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return this.getUser(username);
    }

    private String validatePassword(UserDTO user) {
        String password = null;
        if (Objects.nonNull(user.getPassword())) {
            password = passwordEncoder.encode(user.getPassword());
        } else if (!user.getProvider().equals(Provider.LOCAL)) {
            password = "";
        }

        if (Objects.isNull(password)) {
            throw new BadRequestException("Password cannot be empty.");
        }

        return password;
    }

    private List<Role> validateRoles(List<Role> roles) {
        if (Objects.isNull(roles)) {
            roles = List.of(Role.USER);
        }

        if (!roles.contains(Role.USER)) {
            roles.add(Role.USER);
        }

        return roles;
    }
}
