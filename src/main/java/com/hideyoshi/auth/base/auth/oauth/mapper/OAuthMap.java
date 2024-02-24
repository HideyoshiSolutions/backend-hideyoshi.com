package com.hideyoshi.auth.base.auth.oauth.mapper;

import com.hideyoshi.auth.base.auth.entity.Provider;

public interface OAuthMap {

    String getPrincipal();

    String getProfilePicture();

    Provider getProvider();

}
