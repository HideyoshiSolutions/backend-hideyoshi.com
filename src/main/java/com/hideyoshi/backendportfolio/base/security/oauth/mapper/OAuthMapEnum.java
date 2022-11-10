package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;

public enum OAuthMapEnum {

    GOOGLE(GoogleOAuthMap.class, Provider.GOOGLE),

    GITHUB(GithubOAuthMap.class, Provider.GITHUB);

    private Class oAuthMap;

    private Provider provider;

    private OAuthMapEnum(Class oAuthMap, Provider provider) {
        this.oAuthMap = oAuthMap;
        this.provider = provider;
    }

    public Class getMap() {
        return oAuthMap;
    }

    public Provider getProvider() {
        return provider;
    }

    public static OAuthMapEnum byValue(String name) {
        for (OAuthMapEnum e : values()) {
            if (e.getProvider().getName().equals(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

}
