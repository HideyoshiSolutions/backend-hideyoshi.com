package br.com.hideyoshi.auth.base.auth.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@RequiredArgsConstructor
public class ConfigInterceptor implements WebMvcConfigurer {

    private final UserResourceAccessInterceptor userResourceAccessInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userResourceAccessInterceptor);
    }

}
