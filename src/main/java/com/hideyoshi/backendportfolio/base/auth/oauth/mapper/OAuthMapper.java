package com.hideyoshi.backendportfolio.base.auth.oauth.mapper;

import com.hideyoshi.backendportfolio.base.auth.entity.Provider;
import lombok.Getter;

public enum OAuthMapper {

    GOOGLE(GoogleOAuthMap.class, Provider.GOOGLE),

    GITHUB(GithubOAuthMap.class, Provider.GITHUB);

    private final Class<? extends OAuthMap> oAuthMap;

    @Getter
    private final Provider provider;

    private OAuthMapper(Class<? extends OAuthMap> oAuthMap, Provider provider) {
        this.oAuthMap = oAuthMap;
        this.provider = provider;
    }

    public static OAuthMapper byValue(Provider provider) {
        for (OAuthMapper e : values()) {
            if (e.getProvider().equals(provider)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

    public Class<? extends OAuthMap> getMap() {
        return oAuthMap;
    }

}
