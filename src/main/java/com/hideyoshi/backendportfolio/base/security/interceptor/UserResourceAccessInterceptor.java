package com.hideyoshi.backendportfolio.base.security.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
import com.hideyoshi.backendportfolio.util.exception.BadRequestException;
import com.hideyoshi.backendportfolio.util.guard.UserResourceGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserResourceAccessInterceptor implements HandlerInterceptor {

    private final UserService userService;

    private final ObjectMapper objectMapper;

    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final UserResourceGuard annotation = ((HandlerMethod) handler)
                .getMethodAnnotation(UserResourceGuard.class);

        if (Objects.nonNull(annotation)) {
            Boolean accessPermission =
                    annotation.accessType().hasAccess(this.userService, this.objectMapper, request);
            if (!accessPermission) {
                throw new BadRequestException(annotation.denialMessage());
            }
        }
        return true;
    }

}
