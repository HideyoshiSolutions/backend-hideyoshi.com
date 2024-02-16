package com.hideyoshi.backendportfolio.base.session.service;

import com.hideyoshi.backendportfolio.base.security.model.AuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class SessionManagerServiceImpl implements SessionManagerService {

    @Override
    public AuthDTO validateSession(HttpSession session) {
        return (AuthDTO) session.getAttribute("user");
    }

    @Override
    public void destroySession(HttpSession session) {
        session.invalidate();
    }

}
