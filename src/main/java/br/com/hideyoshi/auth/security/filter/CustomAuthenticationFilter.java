package br.com.hideyoshi.auth.security.filter;

import br.com.hideyoshi.auth.model.UserAuthDTO;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.security.config.RestAuthenticationEntryPointConfig;
import br.com.hideyoshi.auth.security.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

        UserAuthDTO authUser = this.authService.loginUser(
                request,
                response,
                (UserDTO) authentication.getPrincipal()
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authUser);

    }

}