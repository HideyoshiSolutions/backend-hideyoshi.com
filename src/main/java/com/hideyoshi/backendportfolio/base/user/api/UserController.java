package com.hideyoshi.backendportfolio.base.user.api;

import com.hideyoshi.backendportfolio.base.security.service.AuthService;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuard;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuardEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Log4j2
@Controller
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    @GetMapping
    @UserResourceGuard(accessType = UserResourceGuardEnum.ADMIN_USER)
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(this.userService.getUsers());
    }

    @PostMapping("/signup")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<UserDTO> signupUser(@RequestBody @Valid UserDTO user, HttpServletRequest request) {
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/user/signup").toUriString()
        );
        return ResponseEntity.created(uri).body(this.authService.signupUser(user, request));
    }

    @PostMapping("/delete/{id}")
    @UserResourceGuard(accessType = UserResourceGuardEnum.SAME_USER)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
//
//    @PostMapping("/alter/{id}")
//    @UserResourceGuard(accessType = UserResourceGuardEnum.SAME_USER)
//    public ResponseEntity<Void> alterUser(@PathVariable("id") Long id, @RequestBody @Valid UserDTO user) {
//        this.userService.alterUser(id, user);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @PostMapping("/alter/{id}/role/add")
//    @UserResourceGuard(accessType = UserResourceGuardEnum.SAME_USER)
//    public ResponseEntity<?> addRoleToUser(@PathVariable("id") Long id, @RequestBody RoleToUserDTO filter) {
//        userService.addRoleToUser(id, filter.getRoleName());
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/alter/{id}/role/delete")
//    @UserResourceGuard(accessType = UserResourceGuardEnum.SAME_USER)
//    public ResponseEntity<?> deleteRoleToUser(@PathVariable("id") Long id, @RequestBody RoleToUserDTO filter) {
//        userService.removeRoleFromUser(id, filter.getRoleName());
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/login/refresh")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<UserDTO> refreshAccessToken(
            @RequestBody @Valid TokenDTO refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(this.authService.refreshAccessToken(refreshToken.getToken(), request, response));
    }

}
