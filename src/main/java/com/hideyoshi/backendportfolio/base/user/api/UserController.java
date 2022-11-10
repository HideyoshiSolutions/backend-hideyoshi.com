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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.Provider;
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

    @PostMapping("/login/refresh")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<UserDTO> refreshAccessToken(
            @RequestBody @Valid TokenDTO refreshToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(this.authService.refreshAccessToken(refreshToken.getToken(), request, response));
    }

    @GetMapping("/login/callback")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public void oauthCallback(HttpServletResponse response) throws IOException {
        log.info("Teste");
        response.sendRedirect("http://localhost:4200");
    }

    @PostMapping("/delete/{id}")
    @UserResourceGuard(accessType = UserResourceGuardEnum.SAME_USER)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        this.userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
