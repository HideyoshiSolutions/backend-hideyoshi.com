package br.com.hideyoshi.auth.base.auth.oauth.mapper;

import br.com.hideyoshi.auth.base.auth.entity.Provider;
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
