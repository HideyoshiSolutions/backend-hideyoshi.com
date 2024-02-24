package com.hideyoshi.auth.base.session.api;

import com.hideyoshi.auth.base.auth.model.AuthDTO;
import com.hideyoshi.auth.base.session.service.SessionManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/session")
public class SessionController {

    private final SessionManagerService sessionManagerService;

    @GetMapping(path = "/validate")
    public ResponseEntity<AuthDTO> validateCurrentSession(HttpSession session) {
        return ResponseEntity.ok(this.sessionManagerService.validateSession(session));
    }

    @DeleteMapping(path = "/destroy")
    public ResponseEntity<Void> destroyCurrentSession(HttpSession session) {
        this.sessionManagerService.destroySession(session);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
