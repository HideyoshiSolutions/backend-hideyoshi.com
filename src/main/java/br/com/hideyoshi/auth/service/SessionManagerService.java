package br.com.hideyoshi.auth.service;

import br.com.hideyoshi.auth.model.AuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class SessionManagerService {

    public AuthDTO validateSession(HttpSession session) {
        return (AuthDTO) session.getAttribute("user");
    }

    public void destroySession(HttpSession session) {
        session.invalidate();
    }

}
