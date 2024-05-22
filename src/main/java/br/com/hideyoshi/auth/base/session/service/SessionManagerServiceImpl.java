package br.com.hideyoshi.auth.base.session.service;

import br.com.hideyoshi.auth.base.auth.model.AuthDTO;
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
