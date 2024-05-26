package br.com.hideyoshi.auth.security.filter;

import br.com.hideyoshi.auth.security.service.AuthService;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_TYPE_STRING = "Bearer ";

    private final AuthService authService;

    public JWTAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        try {
            this.setUserContext(request);
        } catch (Exception ignored) {
            // ignored
        }
        filterChain.doFilter(request, response);
    }

    private void setUserContext(HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        Authentication loggedUserInfo = this.validateUserAccess(authorizationHeader);

        SecurityContextHolder.getContext().setAuthentication(loggedUserInfo);
    }

    private Authentication validateUserAccess(String authorizationHeader) {
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith(AUTHORIZATION_TYPE_STRING)) {
            String accessToken = authorizationHeader.substring(AUTHORIZATION_TYPE_STRING.length());
            return this.authService.extractAccessTokenInfo(accessToken);
        } else {
            throw new BadRequestException("No authorization header found");
        }
    }
}