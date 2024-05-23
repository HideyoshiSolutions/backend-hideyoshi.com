package br.com.hideyoshi.auth.controller;

import br.com.hideyoshi.auth.model.UserAuthDTO;
import br.com.hideyoshi.auth.service.SessionManagerService;
import br.com.hideyoshi.auth.util.guard.UserResourceGuard;
import br.com.hideyoshi.auth.util.guard.UserResourceGuardEnum;
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
@RequestMapping("/session")
public class SessionController {

    private final SessionManagerService sessionManagerService;

    @GetMapping("/validate")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<UserAuthDTO> validateCurrentSession(HttpSession session) {
        return ResponseEntity.ok(this.sessionManagerService.validateSession(session));
    }

    @DeleteMapping("/destroy")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<Void> destroyCurrentSession(HttpSession session) {
        this.sessionManagerService.destroySession(session);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
