package br.com.hideyoshi.auth.base.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.hideyoshi.auth.base.service.UserService;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidException;
import br.com.hideyoshi.auth.util.guard.UserResourceGuard;
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
                throw new AuthenticationInvalidException(annotation.denialMessage());
            }
        }
        return true;
    }

}
