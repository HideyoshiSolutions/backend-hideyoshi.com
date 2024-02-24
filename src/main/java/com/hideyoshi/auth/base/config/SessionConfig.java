package com.hideyoshi.auth.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

public class SessionConfig {

    @Value("${com.hideyoshi.frontEndPath}")
    private String frontEndPath;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSION");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("(^.+)?(\\.)?(" + frontEndPath + ")((/#!)?(/\\w+)+)?");
        return serializer;
    }

}