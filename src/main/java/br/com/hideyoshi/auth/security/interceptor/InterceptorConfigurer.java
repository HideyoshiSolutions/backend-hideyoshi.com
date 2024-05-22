package br.com.hideyoshi.auth.security.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class InterceptorConfigurer implements WebMvcConfigurer {

    private final UserResourceAccessInterceptor userResourceAccessInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(this.userResourceAccessInterceptor);
    }

}