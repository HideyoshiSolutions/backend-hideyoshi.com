package com.hideyoshi.backendportfolio.base.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.base.security.model.AuthDTO;
import com.hideyoshi.backendportfolio.base.security.oauth.mapper.OAuthMap;
import com.hideyoshi.backendportfolio.base.security.oauth.mapper.OAuthMapper;
import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
import com.hideyoshi.backendportfolio.microservice.storageService.service.StorageService;
import com.hideyoshi.backendportfolio.util.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    @Value("${com.hideyoshi.tokenSecret}")
    private String TOKEN_SECRET;

    @Value("${com.hideyoshi.accessTokenDuration}")
    private Integer ACCESS_TOKEN_DURATION;

    @Value("${com.hideyoshi.refreshTokenDuration}")
    private Integer REFRESH_TOKEN_DURATION;

    private static final String AUTHORIZATION_TYPE_STRING = "Bearer ";

    private final UserService userService;

    private final StorageService storageService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public TokenDTO generateAccessToken(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request) {

        Date expirationDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_DURATION);

        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate)
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities()
                        .stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);

        return new TokenDTO(accessToken, expirationDate);

    }

    @Override
    public TokenDTO generateRefreshToken(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request) {

        Date expirationDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_DURATION);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        return new TokenDTO(refreshToken, expirationDate);

    }

    @Override
    public HashMap<String, TokenDTO> generateTokens(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request) {

        TokenDTO accessToken = generateAccessToken(user, algorithm, request);
        TokenDTO refreshToken = generateRefreshToken(user, algorithm, request);

        HashMap<String, TokenDTO> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @Override
    public UsernamePasswordAuthenticationToken verifyAccessToken(String authorizationHeader) {

        if (!authorizationHeader.startsWith(AUTHORIZATION_TYPE_STRING)) {
            return null;
        }

        String authorizationToken = authorizationHeader.substring(AUTHORIZATION_TYPE_STRING.length());
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(authorizationToken);

        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    @Override
    public AuthDTO generateUserWithTokens(UserDTO user, HttpServletRequest request) {

        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

        HashMap<String, TokenDTO> tokens = this.generateTokens(user, algorithm, request);

        HttpSession httpSession = request.getSession();
        AuthDTO authObject = new AuthDTO(user, tokens.get("accessToken"), tokens.get("refreshToken"));

        httpSession.setAttribute("user", authObject);

        return authObject;
    }

    @Override
    public AuthDTO signupUser(@Valid UserDTO user, HttpServletRequest request) {

        user.setProvider(Provider.LOCAL);

        UserDTO authenticatedUser = this.userService.saveUser(user);

        var profilePicture = this.storageService.getFileUrl(authenticatedUser.getUsername(), "profile");
        profilePicture.ifPresent(
                storageServiceDownloadResponse -> authenticatedUser.setProfilePictureUrl(storageServiceDownloadResponse.getPresignedUrl())
        );

        return this.generateUserWithTokens(
                authenticatedUser,
                request
        );

    }

    @Override
    public void loginUser(HttpServletRequest request, HttpServletResponse response, @Valid UserDTO user) throws IOException {
        var profilePicture = this.storageService.getFileUrl(user.getUsername(), "profile");
        profilePicture.ifPresent(
                storageServiceDownloadResponse -> user.setProfilePictureUrl(storageServiceDownloadResponse.getPresignedUrl())
        );

        AuthDTO authObject = this.generateUserWithTokens(
                user,
                request
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authObject);
    }

    @Override
    public AuthDTO refreshAccessToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        if (!Objects.nonNull(refreshToken)) {
            resolver.resolveException(
                    request,
                    response,
                    null,
                    new BadRequestException("Invalid Refresh Token. Please authenticate first.")
            );
        }

        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        UserDTO user = this.userService.getUser(decodedJWT.getSubject());

        var profilePicture = this.storageService.getFileUrl(user.getUsername(), "profile");
        profilePicture.ifPresent(
                storageServiceDownloadResponse -> user.setProfilePictureUrl(storageServiceDownloadResponse.getPresignedUrl())
        );

        HttpSession httpSession = request.getSession();
        AuthDTO authenticatedUser = new AuthDTO(
                user,
                this.generateAccessToken(user, algorithm, request),
                new TokenDTO(
                        refreshToken,
                        decodedJWT.getExpiresAt()
                )
        );
        httpSession.setAttribute("user", authenticatedUser);

        return authenticatedUser;

    }

    @Override
    public AuthDTO processOAuthPostLogin(@Valid UserDTO user, HttpServletRequest request) {

        if (Objects.nonNull(user.getId())) {
            this.userService.alterUser(user.getId(), user);
        } else {
            this.userService.saveUser(user);
        }

        return this.generateUserWithTokens(user, request);
    }

    @Override
    public void loginOAuthUser(HttpServletRequest request,
                               HttpServletResponse response,
                               OAuth2User oauthUser) throws IOException {

        String clientId = this.getClientFromUrl(request.getRequestURL().toString());

        OAuthMap oauthMap = this.generateOAuthMap(clientId, oauthUser);

        AuthDTO authObject = this.processOAuthPostLogin(
                this.generateUserFromAuthUser(oauthMap, oauthUser),
                request
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper()
                .writeValue(response.getOutputStream(), authObject);
    }

    @Override
    public UserDTO getLoggedUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.getUser(username);
    }

    private String getClientFromUrl(String url) {
        String[] urlPartition = url.split("/");
        return urlPartition[urlPartition.length - 1];
    }

    private OAuthMap generateOAuthMap(String clientId, OAuth2User oauthUser) {
        try {
            return (OAuthMap) OAuthMapper.byValue(clientId).getMap()
                    .getDeclaredConstructor(OAuth2User.class).newInstance(oauthUser);
        } catch (Exception e) {
            throw new BadRequestException("Unsupported OAuth Client.");
        }
    }

    private UserDTO generateUserFromAuthUser(OAuthMap oauthMap, OAuth2User oauthUser) {
        UserDTO user = null;
        try {
            user = this.userService.getUser(oauthMap.getPrincipal());
        } catch (BadRequestException e) {
            user = UserDTO.builder()
                    .name(oauthUser.getAttribute("name"))
                    .username(oauthMap.getPrincipal())
                    .email(oauthUser.getAttribute("email"))
                    .roles(Arrays.asList(Role.USER))
                    .provider(oauthMap.getProvider())
                    .build();
        }
        user.setProfilePictureUrl(oauthMap.getProfilePicture());

        return user;
    }

}
