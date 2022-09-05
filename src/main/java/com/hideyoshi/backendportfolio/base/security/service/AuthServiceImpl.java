package com.hideyoshi.backendportfolio.base.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
import com.hideyoshi.backendportfolio.util.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

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

        HashMap<String,TokenDTO> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @Override
    public UsernamePasswordAuthenticationToken verifyAccessToken(String authorizationHeader) {

        if (authorizationHeader.startsWith(AUTHORIZATION_TYPE_STRING)) {

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
        return null;
    }

    @Override
    public UserDTO refreshAccessToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        if (Objects.nonNull(refreshToken)) {

            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            UserDTO user = this.userService.getUser(decodedJWT.getSubject());

            if (Objects.nonNull(user)) {

                HttpSession httpSession = request.getSession();
                UserDTO authenticatedUser = user.toResponse(
                        this.generateAccessToken(user, algorithm, request),
                        new TokenDTO(
                                refreshToken,
                                decodedJWT.getExpiresAt()
                        )
                );
                httpSession.setAttribute("user", authenticatedUser);

                return authenticatedUser;
            }

        } else {
            resolver.resolveException(
                    request,
                    response,
                    null,
                    new BadRequestException("Invalid Refresh Token. Please authenticate first.")
            );
        }
        return null;
    }

    @Override
    public UserDTO signupUser(@Valid UserDTO user, HttpServletRequest request) {

        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET.getBytes());

        UserDTO userSaved = this.userService.saveUser(user);
        HashMap<String, TokenDTO> tokens = this.generateTokens(userSaved, algorithm, request);

        HttpSession httpSession = request.getSession();
        UserDTO userAuthenticated = userSaved.toResponse(tokens.get("accessToken"), tokens.get("refreshToken"));
        httpSession.setAttribute("user", userAuthenticated);

        return userAuthenticated;
    }

}
