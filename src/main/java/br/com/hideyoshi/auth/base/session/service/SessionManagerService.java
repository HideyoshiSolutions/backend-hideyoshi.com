package br.com.hideyoshi.auth.base.session.service;

import br.com.hideyoshi.auth.base.auth.model.AuthDTO;

import javax.servlet.http.HttpSession;

public interface SessionManagerService {

    AuthDTO validateSession(HttpSession session);

    void destroySession(HttpSession session);

}
