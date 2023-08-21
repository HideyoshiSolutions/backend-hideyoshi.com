package com.hideyoshi.backendportfolio.base.session.service;

import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SessionManagerServiceImpl implements SessionManagerService {

    private final UserService userService;

    @Override
    public UserDTO validateSession(HttpSession session) {

        UserDTO sessionObject = (UserDTO) session.getAttribute("user");

        if (Objects.nonNull(sessionObject)) {
            sessionObject = sessionObject.toResponse(
                    sessionObject.getAccessToken(),
                    sessionObject.getRefreshToken()
            );
        }

        return sessionObject;
    }

    @Override
    public void destroySession(HttpSession session) {
        session.invalidate();
    }

}
