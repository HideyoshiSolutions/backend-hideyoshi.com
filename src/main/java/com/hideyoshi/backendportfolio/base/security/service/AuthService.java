package com.hideyoshi.backendportfolio.base.security.service;

import com.auth0.jwt.algorithms.Algorithm;
import com.hideyoshi.backendportfolio.base.user.model.TokenDTO;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

public interface AuthService {

    TokenDTO generateAccessToken(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request);

    TokenDTO generateRefreshToken(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request);

    HashMap<String,TokenDTO> generateTokens(@Valid UserDTO user, Algorithm algorithm, HttpServletRequest request);

    UsernamePasswordAuthenticationToken verifyAccessToken(String authorizationHeader);

    UserDTO refreshAccessToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    UserDTO signupUser(@Valid UserDTO user, HttpServletRequest request);

    UserDTO generateUserWithTokens(UserDTO user, HttpServletRequest request);

    UserDTO processOAuthPostLogin(@Valid UserDTO user, HttpServletRequest request);

    void loginUser(HttpServletRequest request, HttpServletResponse response, @Valid UserDTO user) throws IOException;

    void loginOAuthUser(HttpServletRequest request, HttpServletResponse response, OAuth2User user) throws IOException;

}
