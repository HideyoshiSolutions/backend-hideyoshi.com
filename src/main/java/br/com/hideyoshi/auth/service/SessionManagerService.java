package br.com.hideyoshi.auth.service;

import br.com.hideyoshi.auth.model.UserAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class SessionManagerService {

    public UserAuthDTO validateSession(HttpSession session) {
        return (UserAuthDTO) session.getAttribute("user");
    }

    public void destroySession(HttpSession session) {
        session.invalidate();
    }

}
