package br.com.hideyoshi.auth.util.guard;

import br.com.hideyoshi.auth.enums.Role;
import br.com.hideyoshi.auth.model.UserDTO;
import br.com.hideyoshi.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Getter
public enum UserResourceGuardEnum {

    USER("user") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                HttpServletRequest request) {
            return UserResourceGuardEnum.justUser(userService, request);
        }
    },

    SAME_USER("same_user") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                HttpServletRequest request) {
            return UserResourceGuardEnum.sameUser(userService, request);
        }
    },

    ADMIN_USER("admin_user") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                HttpServletRequest request) {
            return UserResourceGuardEnum.adminUser(userService, request);
        }
    },

    OPEN("open") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                HttpServletRequest request) {
            return openAccess(userService, request);
        }
    };

    private final String accessType;

    UserResourceGuardEnum(String accessType) {
        this.accessType = accessType;
    }

    public static UserResourceGuardEnum byValue(String accessType) {
        for (UserResourceGuardEnum o : values()) {
            if (o.getAccessType().equals(accessType)) {
                return o;
            }
        }
        throw new IllegalArgumentException("Argument not valid.");
    }

    private static boolean justUser(UserService userService, HttpServletRequest request) {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.getDescription()));
    }

    private static boolean sameUser(UserService userService, HttpServletRequest request) {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Object requestPathVariable = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> pathVariable = objectMapper.convertValue(requestPathVariable, HashMap.class);
        UserDTO userInfo = userService.getUser(Long.parseLong(pathVariable.get("id")));

        return userLogged.getUsername().equals(userInfo.getUsername());

    }

    private static boolean adminUser(UserService userService, HttpServletRequest request) {
        UserDTO userLogged = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.getDescription()));
    }

    private static Boolean openAccess(UserService userService, HttpServletRequest request) {
        return true;
    }

    public abstract Boolean hasAccess(
            UserService userService,
            HttpServletRequest request);

}
