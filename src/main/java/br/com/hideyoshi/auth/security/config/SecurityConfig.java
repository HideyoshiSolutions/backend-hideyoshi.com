package br.com.hideyoshi.auth.security.config;

import br.com.hideyoshi.auth.security.filter.CustomAuthenticationFilter;
import br.com.hideyoshi.auth.security.filter.CustomAuthorizationFilter;
import br.com.hideyoshi.auth.security.service.AuthService;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidException;
import br.com.hideyoshi.auth.util.guard.UserResourceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthService authService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RestAuthenticationEntryPointConfig restAuthenticationEntryPointConfig;
    private final UserResourceHandler userResourceHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .cors().and().csrf().disable();

        this.addSecurityToHttp(http);
//        this.addOAuthSecurityToHttp(http);

        this.configureEndpoints(http);
    }

    private void configureEndpoints(HttpSecurity http) throws Exception {
        for (String endpoint : this.userResourceHandler.getOpenPaths()) {
            http.authorizeRequests().antMatchers(endpoint).permitAll();
        }

        for (String endpoint : this.userResourceHandler.getGuardedPaths()) {
            http.authorizeRequests().antMatchers(endpoint).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN");
        }

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        http.addFilterAfter(
            new CustomAuthorizationFilter(this.authService, this.userResourceHandler),
            UsernamePasswordAuthenticationFilter.class
        );
    }

    private void addSecurityToHttp(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                this.authenticationManager(),
                this.authService,
                this.restAuthenticationEntryPointConfig
        );

        customAuthenticationFilter.setFilterProcessesUrl("/user/login");

        http.addFilter(customAuthenticationFilter);

    }

    //
//    private void addOAuthSecurityToHttp(HttpSecurity http) throws Exception {
//
//        http.oauth2Login()
//                .authorizationEndpoint()
//                .authorizationRequestRepository(this.oAuthRequestRepository)
//                .and().successHandler(this::successHandler)
//                .failureHandler(this::failureHandler);
//    }
//
//    private void successHandler(HttpServletRequest request,
//                                HttpServletResponse response,
//                                Authentication authentication) throws IOException {
//
//        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//
//        AuthDTO authUser = this.authService.loginOAuthUser(oauthUser, request);
//
//        response.setContentType(APPLICATION_JSON_VALUE);
//        new ObjectMapper()
//                .writeValue(response.getOutputStream(), authUser);
//
//    }
//
    private void failureHandler(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException e) {
        throw new AuthenticationInvalidException("Invalid Authentication Attempt.");
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
