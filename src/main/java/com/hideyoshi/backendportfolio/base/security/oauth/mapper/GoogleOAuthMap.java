package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class GoogleOAuthMap implements OAuthMap {

    private OAuth2User oAuth2User;

    @Override
    public String getPrincipal() {
        return this.oAuth2User.getAttribute("given_name");
    }

    @Override
    public String getProfilePicture() {
        return this.oAuth2User.getAttribute("picture");
    }

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

}
