package br.com.hideyoshi.auth.security.service;

import br.com.hideyoshi.auth.enums.Provider;
import br.com.hideyoshi.auth.enums.Role;
import br.com.hideyoshi.auth.model.UserAuthDTO;
import br.com.hideyoshi.auth.model.TokenDTO;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.model.microservice.StorageServiceDownloadResponse;
import br.com.hideyoshi.auth.security.oauth2.handler.OAuthHandler;
import br.com.hideyoshi.auth.security.oauth2.model.OAuthDTO;
import br.com.hideyoshi.auth.service.UserService;
import br.com.hideyoshi.auth.service.microservice.StorageService;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final StorageService storageService;

    private final OAuthHandler oAuthHandler;

    @Value("${br.com.hideyoshi.tokenSecret}")
    private String TOKEN_SECRET;

    @Value("${br.com.hideyoshi.accessTokenDuration}")
    private Integer ACCESS_TOKEN_DURATION;

    @Value("${br.com.hideyoshi.refreshTokenDuration}")
    private Integer REFRESH_TOKEN_DURATION;

    public UserAuthDTO signupUser(@Valid UserDTO user, HttpServletRequest request) {
        user.setProvider(Provider.LOCAL);

        UserDTO authenticatedUser = this.userService.saveUser(user);
        authenticatedUser.setProfilePictureUrl(this.extractProfilePictureUrl(authenticatedUser));

        return this.generateNewAuthenticatedUser(
                authenticatedUser,
                request
        );

    }

    public UserAuthDTO loginUser(HttpServletRequest request, @Valid UserDTO user) throws IOException {
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.generateNewAuthenticatedUser(
                user,
                request
        );
    }

    public UserAuthDTO loginOAuthUser(HttpServletRequest request, OAuth2User oAuth2User) {
        Provider provider = this.oAuthHandler.getProviderFromURL(request.getRequestURL().toString());
        OAuthDTO oAuthDTO = this.oAuthHandler.parseOAuth2User(oAuth2User, provider);

        UserDTO user = this.getUserFromOAuth2User(oAuthDTO);

        return this.processOAuthPostLogin(user, request);
    }

    public UserAuthDTO refreshAccessToken(String requestToken, HttpServletRequest request) {
        DecodedJWT decodedJWT = this.decodeToken(requestToken)
                .orElseThrow(() -> new BadRequestException("Invalid Token"));

        String username = decodedJWT.getSubject();

        UserDTO user = this.userService.getUser(username);
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.refreshAuthenticatedUser(user, request, new TokenDTO(requestToken, decodedJWT.getExpiresAt()));
    }

    public UserAuthDTO validateAccessToken(HttpServletRequest request) {
        UserDTO user = this.getLoggedUser();
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.generateNewAuthenticatedUser(user, request);

    }

    public UserDTO getLoggedUser() {
        return (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Authentication extractAccessTokenInfo(String accessToken) {
        DecodedJWT decodedJWT = this.decodeToken(accessToken)
                .orElseThrow(() -> new BadRequestException("Invalid Token"));

        return new UsernamePasswordAuthenticationToken(
                this.userService.getUser(decodedJWT.getSubject()),
                null,
                stream(decodedJWT.getClaim("roles").asArray(String.class))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }

    private Optional<DecodedJWT> decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return Optional.of(verifier.verify(token));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private UserDTO getUserFromOAuth2User(OAuthDTO oAuth2User) {
        UserDTO user;

        try {
            user = this.userService.getUser(oAuth2User.getUsername());
            user.setName(oAuth2User.getName());
            user.setProfilePictureUrl(oAuth2User.getProfilePictureUrl());
        } catch (BadRequestException e) {
            user = UserDTO.builder()
                    .name(oAuth2User.getName())
                    .username(oAuth2User.getUsername())
                    .email(oAuth2User.getEmail())
                    .roles(List.of(Role.USER))
                    .provider(oAuth2User.getProvider())
                    .profilePictureUrl(oAuth2User.getProfilePictureUrl())
                    .build();
        }

        return user;
    }

    private UserAuthDTO processOAuthPostLogin(@Valid UserDTO user, HttpServletRequest request) {

        if (Objects.nonNull(user.getId())) {
            this.userService.alterUser(user.getId(), user);
        } else {
            this.userService.saveUser(user);
        }

        return this.generateNewAuthenticatedUser(user, request);
    }

    private String extractProfilePictureUrl(UserDTO user) {
        return this.storageService.getFileUrl(user.getUsername(), "profile")
                .map(StorageServiceDownloadResponse::getSignedUrl)
                .orElse(null);
    }

    private UserAuthDTO generateNewAuthenticatedUser(UserDTO user, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        UserAuthDTO authObject = new UserAuthDTO(
                user,
                this.generateToken(user, request, ACCESS_TOKEN_DURATION),
                this.generateToken(user, request, REFRESH_TOKEN_DURATION)
        );

        httpSession.setAttribute("user", authObject);

        return authObject;
    }

    private UserAuthDTO refreshAuthenticatedUser(UserDTO user, HttpServletRequest request, TokenDTO refreshToken) {
        HttpSession httpSession = request.getSession();
        UserAuthDTO authObject = new UserAuthDTO(
                user,
                this.generateToken(user, request, ACCESS_TOKEN_DURATION),
                refreshToken
        );

        httpSession.setAttribute("user", authObject);

        return authObject;
    }

    private TokenDTO generateToken(@Valid UserDTO user, HttpServletRequest request, Integer duration) {

        Date expirationDate = new Date(System.currentTimeMillis() + duration);
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

        String token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);

        return new TokenDTO(token, expirationDate);

    }

}
