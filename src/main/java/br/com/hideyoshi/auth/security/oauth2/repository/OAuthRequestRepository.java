package br.com.hideyoshi.auth.security.oauth2.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Log4j2
@Repository
@RequiredArgsConstructor
public class OAuthRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final RedisTemplate<String, OAuth2AuthorizationRequest> template;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String state = request.getParameter("state");
        if (Objects.nonNull(state)) {
            return removeAuthorizationRequest(request);
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        this.saveAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {

        String state = request.getParameter("state");

        OAuth2AuthorizationRequest authorizationRequest = null;
        if (Objects.nonNull(state)) {
            authorizationRequest = this.getAuthorizationRequestFromSession(state);
        }

        if (Objects.nonNull(authorizationRequest)) {
            removeAuthorizationRequestFromSession(state);
            return authorizationRequest;
        }
        return null;
    }

    private void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        this.template.opsForValue().set(
                String.format("state_%s", authorizationRequest.getState()),
                authorizationRequest
        );
    }

    private OAuth2AuthorizationRequest getAuthorizationRequestFromSession(String state) {
        return this.template.opsForValue().get(String.format("state_%s", state));
    }

    private void removeAuthorizationRequestFromSession(String state) {
        this.template.delete(String.format("state_%s", state));
    }

}