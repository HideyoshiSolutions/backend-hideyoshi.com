package com.hideyoshi.backendportfolio.healthChecker.api;


import com.hideyoshi.backendportfolio.util.guard.UserResourceGuard;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuardEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthCheckerController {
    @RequestMapping
    @UserResourceGuard(accessType = UserResourceGuardEnum.OPEN)
    public ResponseEntity<String> healthCheck() {
        log.info("Health check requested");
        return ResponseEntity.ok("Health check successful!");
    }
}
