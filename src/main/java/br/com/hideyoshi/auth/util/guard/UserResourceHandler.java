package br.com.hideyoshi.auth.util.guard;


import br.com.hideyoshi.auth.enums.Role;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class UserResourceHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    public Boolean hasAccess(UserResourceGuardEnum userResourceGuardEnum, HttpServletRequest request) {
        return switch (userResourceGuardEnum) {
            case USER -> justUser();
            case SAME_USER -> sameUser(request);
            case ADMIN_USER -> adminUser();
            case OPEN -> openAccess();
            default -> false;
        };
    }

    private boolean justUser() {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.getDescription()));
    }

    private boolean sameUser(HttpServletRequest request) {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Object requestPathVariable = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        HashMap<String, String> pathVariable = this.objectMapper.convertValue(
                requestPathVariable, new TypeReference<>() {}
        );

        UserDTO userInfo = this.userService.getUser(Long.parseLong(pathVariable.get("id")));

        return userLogged.getUsername().equals(userInfo.getUsername());

    }

    private boolean adminUser() {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.getDescription()));
    }

    private Boolean openAccess() {
        return true;
    }
}
