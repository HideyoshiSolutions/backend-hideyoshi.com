package br.com.hideyoshi.auth.security.config;

import br.com.hideyoshi.auth.model.AuthDTO;
import br.com.hideyoshi.auth.security.filter.CustomAuthenticationFilter;
import br.com.hideyoshi.auth.security.filter.CustomAuthorizationFilter;
import br.com.hideyoshi.auth.security.service.AuthService;
import br.com.hideyoshi.auth.util.exception.AuthenticationInvalidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private HandlerExceptionResolver resolver;

    private final AuthService authService;

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final RestAuthenticationEntryPointConfig restAuthenticationEntryPointConfig;

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

        this.configureOpenEndpoints(http);
    }

    private void configureOpenEndpoints(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/session/**").permitAll()
                .and().authorizeRequests().antMatchers("/health").permitAll()
                .and().authorizeRequests().antMatchers("/user/signup").permitAll()
                .and().authorizeRequests().antMatchers("/user/oauth/**").permitAll()
                .and().authorizeRequests().antMatchers("/user/login/**").permitAll()
                .and().authorizeRequests().antMatchers("/session/**").permitAll()
                .and().authorizeRequests().antMatchers("/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().addFilterAfter(new CustomAuthorizationFilter(this.authService), UsernamePasswordAuthenticationFilter.class);
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
