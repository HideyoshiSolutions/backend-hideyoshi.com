package com.hideyoshi.backendportfolio.base.session.service;

import com.hideyoshi.backendportfolio.base.security.model.AuthDTO;

import javax.servlet.http.HttpSession;

public interface SessionManagerService {

    AuthDTO validateSession(HttpSession session);

    void destroySession(HttpSession session);

}
