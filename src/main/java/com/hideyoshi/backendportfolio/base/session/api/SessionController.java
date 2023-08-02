package com.hideyoshi.backendportfolio.base.session.api;

import com.hideyoshi.backendportfolio.base.session.service.SessionManagerService;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/session")
public class SessionController {

    private final SessionManagerService sessionManagerService;

    @GetMapping(path = "/validate")
    public ResponseEntity<UserDTO> validateCurrentSession(HttpSession session) {
        return ResponseEntity.ok(this.sessionManagerService.validateSession(session));
    }

    @DeleteMapping(path="/destroy")
    public ResponseEntity<Void> destroyCurrentSession(HttpSession session) {
        this.sessionManagerService.destroySession(session);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
