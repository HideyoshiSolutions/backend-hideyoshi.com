package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
public class GithubOAuthMap implements OAuthMap {

    private OAuth2User oAuth2User;

    @Override
    public String getPrincipal() {
        return oAuth2User.getAttribute("login");
    }

    @Override
    public String getProfilePicture() {
        return this.oAuth2User.getAttribute("avatar_url");
    }

    @Override
    public Provider getProvider() {
        return Provider.GITHUB;
    }

}
