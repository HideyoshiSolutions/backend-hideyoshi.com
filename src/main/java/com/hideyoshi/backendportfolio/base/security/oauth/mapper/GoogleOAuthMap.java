package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class GoogleOAuthMap implements OAuthMap {

    private OAuth2User oauthUser;

    @Override
    public String getPrincipal() {
        return this.oauthUser.getAttribute("given_name");
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }


}
