package br.com.hideyoshi.auth.security.filter;

import br.com.hideyoshi.auth.security.service.AuthService;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidException;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidExceptionDetails;
import br.com.hideyoshi.auth.util.guard.UserResourceHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_TYPE_STRING = "Bearer ";

    private final AuthService authService;

    private final UserResourceHandler userResourceHandler;

    public CustomAuthorizationFilter(AuthService authService, UserResourceHandler userResourceHandler) {
        this.authService = authService;
        this.userResourceHandler = userResourceHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!this.isPathGuarded(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            this.setUserContext(request);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setHeader("error", e.getMessage());
            response.setStatus(FORBIDDEN.value());

            AuthenticationInvalidExceptionDetails error = new AuthenticationInvalidExceptionDetails("Authentication Failed. Check your credentials.",
                    HttpStatus.FORBIDDEN.value(), e.getMessage(),
                    e.getClass().getName(), LocalDateTime.now());

            response.setContentType(APPLICATION_JSON_VALUE);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            mapper.writeValue(response.getOutputStream(), error);
        }
    }

    private void setUserContext(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        UsernamePasswordAuthenticationToken authenticationToken =
                this.validateUserAccess(authorizationHeader);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UsernamePasswordAuthenticationToken validateUserAccess(String authorizationHeader) {
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith(AUTHORIZATION_TYPE_STRING)) {
            String accessToken = authorizationHeader.substring(AUTHORIZATION_TYPE_STRING.length());
            return this.authService.extractAccessTokenInfo(accessToken);
        } else {
            throw new AuthenticationInvalidException("Access denied");
        }
    }

    private boolean isPathGuarded(String path) {
        return this.userResourceHandler.getGuardedPaths().stream()
                .anyMatch(p -> isPatternMatchUri(p, path));
    }

    private boolean isPatternMatchUri(String pattern, String url) {
        PathPatternParser pathPatternParser = new PathPatternParser();
        PathPattern pathPattern = pathPatternParser.parse(pattern);
        PathContainer pathContainer = PathContainer.parsePath(url);
        return pathPattern.matches(pathContainer);
    }
}