package br.com.hideyoshi.auth.base.config;

import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Component("restAuthenticationEntryPoint")
public class RestAuthenticationEntryPointConfig implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) {

        resolver.resolveException(
                request,
                response,
                null,
                new AuthenticationInvalidException("Authentication Failed. Check your credentials.")
        );

    }
}
