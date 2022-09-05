package com.hideyoshi.backendportfolio.base.security;

import com.hideyoshi.backendportfolio.base.config.RestAuthenticationEntryPointConfig;
import com.hideyoshi.backendportfolio.base.security.filter.CustomAuthenticationFilter;
import com.hideyoshi.backendportfolio.base.security.filter.CustomAuthorizationFilter;
import com.hideyoshi.backendportfolio.base.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthService authService;

    private final UserDetailsService userDetailsService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final RestAuthenticationEntryPointConfig restAuthenticationEntryPointConfig;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(this.authenticationManager(), this.authService, this.restAuthenticationEntryPointConfig);

        customAuthenticationFilter.setFilterProcessesUrl("/user/login");

        http.cors().and().csrf().disable()
                .authorizeRequests().antMatchers("/session/**").permitAll()
                .and().authorizeRequests().antMatchers("/user/signup").permitAll()
                .and().authorizeRequests().antMatchers("/user/login/refresh").permitAll()
                .and().authorizeRequests().antMatchers("/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().addFilter(customAuthenticationFilter)
                .addFilterBefore(new CustomAuthorizationFilter(this.authService), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
