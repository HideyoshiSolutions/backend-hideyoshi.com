package br.com.hideyoshi.auth.base.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.hideyoshi.auth.base.config.RestAuthenticationEntryPointConfig;
import br.com.hideyoshi.auth.base.auth.model.AuthDTO;
import br.com.hideyoshi.auth.base.auth.service.AuthService;
import br.com.hideyoshi.auth.base.auth.model.UserDTO;
import lombok.extern.log4j.Log4j2;
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

        AuthDTO authUser = this.authService.loginUser(
                request,
                response,
                (UserDTO) authentication.getPrincipal()
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authUser);

    }

}
