package com.hideyoshi.backendportfolio.base.security.oauth.repo;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Log4j2
@Repository
public class OAuthRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

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

        String state = authorizationRequest.getState();

        request.getSession().setAttribute(
            String.format("state_%s", state),
            authorizationRequest
        );

    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {

        String state = request.getParameter("state");

        OAuth2AuthorizationRequest authorizationRequest = null;
        if (Objects.nonNull(state)) {
            authorizationRequest = this.getAuthorizationRequestFromSession(request, state);
        }

        if (Objects.nonNull(authorizationRequest)) {
            removeAuthorizationRequestFromSession(request, state);
            return authorizationRequest;
        }
        return null;
    }

    private OAuth2AuthorizationRequest getAuthorizationRequestFromSession(HttpServletRequest request, String state) {
        return (OAuth2AuthorizationRequest) request.getSession().getAttribute(
                String.format("state_%s", state)
        );
    }

    private void removeAuthorizationRequestFromSession(HttpServletRequest request, String state) {
        request.getSession().removeAttribute(
                String.format("state_%s", state)
        );
    }

}
