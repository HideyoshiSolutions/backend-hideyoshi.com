package com.hideyoshi.backendportfolio.base.security.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.base.config.RestAuthenticationEntryPointConfig;
import com.hideyoshi.backendportfolio.base.security.service.AuthService;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final RestAuthenticationEntryPointConfig restAuthenticationEntryPointConfig;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, AuthService authService, RestAuthenticationEntryPointConfig restAuthenticationEntryPointConfig) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.restAuthenticationEntryPointConfig = restAuthenticationEntryPointConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Authentication userAuthentication = null;
        try {
            userAuthentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            restAuthenticationEntryPointConfig.commence(request, response, e);
        }
        return userAuthentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        UserDTO user = (UserDTO) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        HashMap<String,TokenDTO> tokens = this.authService.generateTokens(user, algorithm, request);

        HttpSession httpSession = request.getSession();
        UserDTO authenticatedUser = user.toResponse(tokens.get("accessToken"), tokens.get("refreshToken"));
        httpSession.setAttribute("user", authenticatedUser);

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authenticatedUser);
    }
    
}
