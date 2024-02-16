package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import lombok.Getter;

public enum OAuthMapper {

    GOOGLE(GoogleOAuthMap.class, Provider.GOOGLE),

    GITHUB(GithubOAuthMap.class, Provider.GITHUB);

    private final Class oAuthMap;

    @Getter
    private final Provider provider;

    private OAuthMapper(Class oAuthMap, Provider provider) {
        this.oAuthMap = oAuthMap;
        this.provider = provider;
    }

    public static OAuthMapper byValue(String name) {
        for (OAuthMapper e : values()) {
            if (e.getProvider().getName().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

    public Class getMap() {
        return oAuthMap;
    }

}
