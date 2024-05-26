package br.com.hideyoshi.auth.security.config;

import br.com.hideyoshi.auth.model.UserAuthDTO;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.security.filter.JWTAuthenticationFilter;
import br.com.hideyoshi.auth.security.oauth2.repository.OAuthRequestRepository;
import br.com.hideyoshi.auth.security.service.AuthService;
import br.com.hideyoshi.auth.service.UserService;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidExceptionDetails;
import br.com.hideyoshi.auth.util.guard.UserResourceHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserService userService;
    private final OAuthRequestRepository oAuthRequestRepository;
    private final UserResourceHandler userResourceHandler;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(this.userService);
        provider.setPasswordEncoder(this.passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().and().csrf().disable();

        this.addEndpointSecurityToHttp(http);
        this.addOAuthSecurityToHttp(http);

        this.configureEndpoints(http);

        return http.build();
    }

    private void configureEndpoints(HttpSecurity http) throws Exception {
        for (String endpoint : this.userResourceHandler.getOpenPaths()) {
            http.authorizeRequests().antMatchers(endpoint).permitAll();
        }

        for (String endpoint : this.userResourceHandler.getGuardedPaths()) {
            http.authorizeRequests().antMatchers(endpoint).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        }

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.addFilterBefore(
            new JWTAuthenticationFilter(this.authService),
            UsernamePasswordAuthenticationFilter.class
        );
    }

    private void addEndpointSecurityToHttp(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginProcessingUrl("/user/login")
                .successHandler(this::successFormHandler)
                .failureHandler(this::failureHandler)
        );

        http.authorizeRequests().antMatchers("/login").denyAll();
    }

    private void addOAuthSecurityToHttp(HttpSecurity http) throws Exception {

        http.oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(this.oAuthRequestRepository)
                .and().successHandler(this::successOAuth2Handler)
                .failureHandler(this::failureHandler);
    }

    private void successFormHandler(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        UserAuthDTO authUser = this.authService.loginUser(
            request,
            (UserDTO) authentication.getPrincipal()
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authUser);
    }

    private void successOAuth2Handler(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        UserAuthDTO authUser = this.authService.loginOAuthUser(
                request,
                (OAuth2User) authentication.getPrincipal()
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authUser);

    }

    private void failureHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) throws IOException {
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
