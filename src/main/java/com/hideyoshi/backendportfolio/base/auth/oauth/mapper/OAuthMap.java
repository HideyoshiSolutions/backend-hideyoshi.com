package com.hideyoshi.backendportfolio.base.auth.oauth.mapper;

import com.hideyoshi.backendportfolio.base.auth.entity.Provider;

public interface OAuthMap {

    String getPrincipal();

    String getProfilePicture();

    Provider getProvider();

}
