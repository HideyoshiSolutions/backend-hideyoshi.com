package br.com.hideyoshi.auth.base.service;

import br.com.hideyoshi.auth.base.model.AuthDTO;
import br.com.hideyoshi.auth.base.model.TokenDTO;
import br.com.hideyoshi.auth.base.oauth.mapper.OAuthMap;
import br.com.hideyoshi.auth.base.oauth.mapper.OAuthMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import br.com.hideyoshi.auth.base.entity.Provider;
import br.com.hideyoshi.auth.base.entity.Role;
import br.com.hideyoshi.auth.base.model.UserDTO;
import br.com.hideyoshi.auth.microservice.storageservice.model.StorageServiceDownloadResponse;
import br.com.hideyoshi.auth.microservice.storageservice.service.StorageService;
import br.com.hideyoshi.auth.util.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private static final String AUTHORIZATION_TYPE_STRING = "Bearer ";

    private final UserService userService;

    private final StorageService storageService;

    @Value("${com.hideyoshi.tokenSecret}")
    private String TOKEN_SECRET;

    @Value("${com.hideyoshi.accessTokenDuration}")
    private Integer ACCESS_TOKEN_DURATION;

    @Value("${com.hideyoshi.refreshTokenDuration}")
    private Integer REFRESH_TOKEN_DURATION;

    public AuthDTO signupUser(@Valid UserDTO user, HttpServletRequest request) {
        user.setProvider(Provider.LOCAL);

        UserDTO authenticatedUser = this.userService.saveUser(user);
        authenticatedUser.setProfilePictureUrl(this.extractProfilePictureUrl(authenticatedUser));

        return this.generateNewAuthenticatedUser(
                authenticatedUser,
                request
        );

    }

    public AuthDTO loginUser(HttpServletRequest request, HttpServletResponse response, @Valid UserDTO user) throws IOException {
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.generateNewAuthenticatedUser(
                user,
                request
        );
    }

    public AuthDTO loginOAuthUser(OAuth2User oauthUser, HttpServletRequest request) {
        Provider clientProvider = Provider.byValue(
                this.getClientFromUrl(request.getRequestURL().toString())
        );

        OAuthMap oauthMap = this.generateOAuthMap(clientProvider, oauthUser);

        return this.processOAuthPostLogin(
                this.generateAuthenticatedUserFromOAuth(oauthMap, oauthUser),
                request
        );
    }

    public AuthDTO refreshAccessToken(String requestToken, HttpServletRequest request) {
        DecodedJWT decodedJWT = this.decodeToken(requestToken)
                .orElseThrow(() -> new BadRequestException("Invalid Token"));

        String username = decodedJWT.getSubject();

        UserDTO user = this.userService.getUser(username);
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.refreshAuthenticatedUser(user, request, new TokenDTO(requestToken, decodedJWT.getExpiresAt()));
    }

    public AuthDTO validateAccessToken(HttpServletRequest request) {
        UserDTO user = this.getLoggedUser();
        user.setProfilePictureUrl(this.extractProfilePictureUrl(user));

        return this.generateNewAuthenticatedUser(user, request);

    }

    public UserDTO getLoggedUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(username);
    }

    public UsernamePasswordAuthenticationToken extractAccessTokenInfo(String accessToken) {
        DecodedJWT decodedJWT = this.decodeToken(accessToken)
                .orElseThrow(() -> new BadRequestException("Invalid Token"));

        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private Optional<DecodedJWT> decodeToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return Optional.of(verifier.verify(token));
        } catch (Exception e) {
            log.warn("Token verification failed: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private AuthDTO processOAuthPostLogin(@Valid UserDTO user, HttpServletRequest request) {

        if (Objects.nonNull(user.getId())) {
            this.userService.alterUser(user.getId(), user);
        } else {
            this.userService.saveUser(user);
        }

        return this.generateNewAuthenticatedUser(user, request);
    }

    private String getClientFromUrl(String url) {
        String[] urlPartition = url.split("/");
        return urlPartition[urlPartition.length - 1];
    }

    private OAuthMap generateOAuthMap(Provider clientProvider, OAuth2User oauthUser) {
        try {
            return OAuthMapper.byValue(clientProvider).getMap()
                    .getDeclaredConstructor(OAuth2User.class).newInstance(oauthUser);
        } catch (Exception e) {
            throw new BadRequestException("Unsupported OAuth Client.");
        }
    }

    private String extractProfilePictureUrl(UserDTO user) {
        return this.storageService.getFileUrl(user.getUsername(), "profile")
                .map(StorageServiceDownloadResponse::getPresignedUrl)
                .orElse(null);
    }

    private UserDTO generateAuthenticatedUserFromOAuth(OAuthMap oauthMap, OAuth2User oauthUser) {
        UserDTO user;
        try {
            user = this.userService.getUser(oauthMap.getPrincipal());
        } catch (BadRequestException e) {
            user = UserDTO.builder()
                    .name(oauthUser.getAttribute("name"))
                    .username(oauthMap.getPrincipal())
                    .email(oauthUser.getAttribute("email"))
                    .roles(List.of(Role.USER))
                    .provider(oauthMap.getProvider())
                    .build();
        }
        user.setProfilePictureUrl(oauthMap.getProfilePicture());

        return user;
    }

    private AuthDTO generateNewAuthenticatedUser(UserDTO user, HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        AuthDTO authObject = new AuthDTO(
                user,
                this.generateToken(user, request, ACCESS_TOKEN_DURATION),
                this.generateToken(user, request, REFRESH_TOKEN_DURATION)
        );

        httpSession.setAttribute("user", authObject);

        return authObject;
    }

    private AuthDTO refreshAuthenticatedUser(UserDTO user, HttpServletRequest request, TokenDTO refreshToken) {
        HttpSession httpSession = request.getSession();
        AuthDTO authObject = new AuthDTO(
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
