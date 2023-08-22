package com.hideyoshi.backendportfolio.base.security.oauth.mapper;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthMap {

    String getPrincipal();

    String getProfilePicture();

    Provider getProvider();

}
