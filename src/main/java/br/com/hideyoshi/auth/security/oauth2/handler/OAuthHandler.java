package br.com.hideyoshi.auth.security.oauth2.handler;

import br.com.hideyoshi.auth.enums.Provider;
import br.com.hideyoshi.auth.security.oauth2.model.OAuthDTO;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class OAuthHandler {
    public Provider getProviderFromURL(String url) {
        String[] urlPartition = url.split("/");
        return Provider.byValue(urlPartition[urlPartition.length - 1]);
    }

    public OAuthDTO parseOAuth2User(OAuth2User user, Provider provider) {
        return switch (provider) {
            case GITHUB -> parseFromGithub(user);
            case GOOGLE -> parseFromGoogle(user);
            default -> throw new BadRequestException("Provider not supported.");
        };
    }

    private OAuthDTO parseFromGithub(OAuth2User user) {
        return new OAuthDTO(
                user.getAttribute("name"),
                user.getAttribute("login"),
                user.getAttribute("email"),
                user.getAttribute("avatar_url"),
                Provider.GITHUB
        );
    }

    private OAuthDTO parseFromGoogle(OAuth2User user) {
        return new OAuthDTO(
                user.getAttribute("name"),
                user.getAttribute("given_name"),
                user.getAttribute("email"),
                user.getAttribute("picture"),
                Provider.GOOGLE
        );
    }
}
