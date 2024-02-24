package com.hideyoshi.backendportfolio.base.auth.api;

import com.hideyoshi.backendportfolio.base.auth.model.AuthDTO;
import com.hideyoshi.backendportfolio.base.auth.service.AuthService;
import com.hideyoshi.backendportfolio.base.auth.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.auth.model.UserDTO;
import com.hideyoshi.backendportfolio.base.auth.service.UserService;
import com.hideyoshi.backendportfolio.microservice.storageService.enums.FileTypeEnum;
import com.hideyoshi.backendportfolio.microservice.storageService.model.StorageServiceUploadResponse;
import com.hideyoshi.backendportfolio.microservice.storageService.service.StorageService;
import com.hideyoshi.backendportfolio.util.exception.BadRequestException;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuard;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuardEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
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

    private final StorageService storageService;

    @GetMapping
    @UserResourceGuard(accessType = UserResourceGuardEnum.ADMIN_USER)
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(this.userService.getUsers());
    }

    @PostMapping("/signup")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<AuthDTO> signupUser(@RequestBody @Valid UserDTO user, HttpServletRequest request) {
        URI uri = URI.create(
                ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/user/signup").toUriString()
        );
        return ResponseEntity.created(uri).body(this.authService.signupUser(user, request));
    }

    @PostMapping("/login/refresh")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<AuthDTO> refreshAccessToken(
            @RequestBody @Valid TokenDTO refreshToken,
            HttpServletRequest request) {
        return ResponseEntity.ok(this.authService.refreshAccessToken(refreshToken.getToken(), request));
    }

    @PostMapping("/login/validate")
    @UserResourceGuard(accessType = UserResourceGuardEnum.USER)
    public ResponseEntity<AuthDTO> validateAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(this.authService.validateAccessToken(request));
    }

    @DeleteMapping("/delete")
    @UserResourceGuard(accessType = UserResourceGuardEnum.USER)
    public ResponseEntity<Void> deleteMyUser() {
        UserDTO loggedUser = this.authService.getLoggedUser();

        this.userService.deleteUser(loggedUser.getId());
        this.storageService.deleteFile(loggedUser.getUsername(), "profile");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    @UserResourceGuard(accessType = UserResourceGuardEnum.ADMIN_USER)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        UserDTO user = this.userService.getUser(id);

        this.userService.deleteUser(user.getId());
        this.storageService.deleteFile(user.getUsername(), "profile");

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/profile-picture")
    @UserResourceGuard(accessType = UserResourceGuardEnum.USER)
    public StorageServiceUploadResponse addProfilePicture(
            @RequestParam FileTypeEnum fileType
    ) {
        UserDTO user = this.authService.getLoggedUser();
        return this.storageService.getNewFileUrl(
                user.getUsername(),
                "profile",
                fileType
        ).orElseThrow(() -> new BadRequestException("File not found"));
    }

    @DeleteMapping("/profile-picture")
    @UserResourceGuard(accessType = UserResourceGuardEnum.USER)
    public void deleteProfilePicture() {
        UserDTO user = this.authService.getLoggedUser();
        this.storageService.deleteFile(
                user.getUsername(),
                "profile"
        );
    }

    @PostMapping("/profile-picture/proccess")
    @UserResourceGuard(accessType = UserResourceGuardEnum.USER)
    public void processProfilePicture() {
        UserDTO user = this.authService.getLoggedUser();
        this.storageService.processFile(
                user.getUsername(),
                "profile"
        );
    }

}
