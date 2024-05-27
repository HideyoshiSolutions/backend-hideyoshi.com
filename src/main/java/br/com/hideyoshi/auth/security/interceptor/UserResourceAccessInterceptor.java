package br.com.hideyoshi.auth.security.interceptor;

import br.com.hideyoshi.auth.util.exception.AuthorizationException;
import br.com.hideyoshi.auth.util.guard.UserResourceGuard;
import br.com.hideyoshi.auth.util.guard.UserResourceHandler;
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

    private final UserResourceHandler userResourceHandler;

    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final UserResourceGuard annotation = ((HandlerMethod) handler)
                .getMethodAnnotation(UserResourceGuard.class);

        if (Objects.nonNull(annotation)) {
            Boolean accessPermission = this.userResourceHandler.hasAccess(
                annotation.accessType(), request
            );

            if (!accessPermission) {
                throw new AuthorizationException(annotation.denialMessage());
            }
        }
        return true;
    }

}