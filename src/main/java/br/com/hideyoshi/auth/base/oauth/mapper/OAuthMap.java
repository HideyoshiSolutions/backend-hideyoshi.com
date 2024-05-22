package br.com.hideyoshi.auth.base.oauth.mapper;

import br.com.hideyoshi.auth.base.entity.Provider;

public interface OAuthMap {

    String getPrincipal();

    String getProfilePicture();

    Provider getProvider();

}
