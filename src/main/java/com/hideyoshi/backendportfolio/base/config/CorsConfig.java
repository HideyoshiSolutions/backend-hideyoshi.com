package com.hideyoshi.backendportfolio.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${com.hideyoshi.frontEndPath}")
    private String FRONTEND_PATH;

    @Value("${com.hideyoshi.frontendConnectionType}")
    private String CONNECTION_TYPE;

    private final String HTTP = "http://";

    private final String HTTPS = "https://";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        String connectionProtocol = CONNECTION_TYPE.equals("secure")
                ? HTTPS
                : HTTP;

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(connectionProtocol + FRONTEND_PATH));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
