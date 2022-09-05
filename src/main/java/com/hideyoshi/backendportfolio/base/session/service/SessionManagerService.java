package com.hideyoshi.backendportfolio.base.session.service;

import com.hideyoshi.backendportfolio.base.user.model.UserDTO;

import javax.servlet.http.HttpSession;

public interface SessionManagerService {

    UserDTO validateSession(HttpSession session);

    void destroySession(HttpSession session);

}
