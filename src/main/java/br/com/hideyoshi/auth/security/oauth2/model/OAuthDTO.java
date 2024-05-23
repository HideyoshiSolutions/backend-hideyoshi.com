package br.com.hideyoshi.auth.security.oauth2.model;

import br.com.hideyoshi.auth.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuthDTO {
    private String name;

    private String username;

    private String email;

    private String profilePictureUrl;

    private Provider provider;
}
