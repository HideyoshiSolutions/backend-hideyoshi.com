package br.com.hideyoshi.auth.controller;


import br.com.hideyoshi.auth.util.guard.UserResourceGuard;
import br.com.hideyoshi.auth.util.guard.UserResourceGuardEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Log4j2
@Controller
public class HealthCheckerController {
    @RequestMapping("/health")
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<String> healthCheck() {
        log.info("Health check requested");
        return ResponseEntity.ok("Health check successful!");
    }
}
