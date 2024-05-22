package br.com.hideyoshi.auth.base.auth.oauth.mapper;

import br.com.hideyoshi.auth.base.auth.entity.Provider;

public interface OAuthMap {

    String getPrincipal();

    String getProfilePicture();

    Provider getProvider();

}
