package com.hideyoshi.backendportfolio.base.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.base.security.service.AuthService;
import com.hideyoshi.backendportfolio.util.exception.AuthenticationInvalidException;
import com.hideyoshi.backendportfolio.util.exception.AuthenticationInvalidExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final List<String> notProtectedPaths = Arrays.asList(
            "/health",
            "/user/login",
            "/user/signup",
            "/user/login/refresh",
            "/session/validate",
            "/session/destroy"
    );

    private static final String AUTHORIZATION_TYPE_STRING = "Bearer ";

    private final AuthService authService;

    public CustomAuthorizationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (this.isPathNotProtected(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    this.validateUserAccess(authorizationHeader);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setHeader("error", e.getMessage());
            response.setStatus(FORBIDDEN.value());

            AuthenticationInvalidExceptionDetails error = new AuthenticationInvalidExceptionDetails("Authentication Failed. Check your credentials.",
                                HttpStatus.FORBIDDEN.value(), e.getMessage(),
                                e.getClass().getName(), LocalDateTime.now());

            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper()
                    .writeValue(response.getOutputStream(), error);
        }
    }

    private Boolean isPathNotProtected(String path) {
        return notProtectedPaths.contains(path);
    }

    private UsernamePasswordAuthenticationToken validateUserAccess(String authorizationHeader) {
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith(AUTHORIZATION_TYPE_STRING)) {
            String accessToken = authorizationHeader.substring(AUTHORIZATION_TYPE_STRING.length());
            return this.authService.extractAccessTokenInfo(accessToken);
        } else {
            throw new AuthenticationInvalidException("Access denied");
        }
    }
}
