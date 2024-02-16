package com.hideyoshi.backendportfolio.util.guard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hideyoshi.backendportfolio.base.user.entity.Role;
import com.hideyoshi.backendportfolio.base.user.model.UserDTO;
import com.hideyoshi.backendportfolio.base.user.service.UserService;
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
                ObjectMapper objectMapper,
                HttpServletRequest request) {
            return justUser(userService, objectMapper, request);
        }
    },

    SAME_USER("same_user") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                ObjectMapper objectMapper,
                HttpServletRequest request) {
            return sameUser(userService, objectMapper, request);
        }
    },

    ADMIN_USER("admin_user") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                ObjectMapper objectMapper,
                HttpServletRequest request) {
            return adminUser(userService, objectMapper, request);
        }
    },

    OPEN("open") {
        @Override
        public Boolean hasAccess(
                UserService userService,
                ObjectMapper objectMapper,
                HttpServletRequest request) {
            return openAccess(userService, objectMapper, request);
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

    private static boolean justUser(UserService userService, ObjectMapper objectMapper, HttpServletRequest request) {

        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userLogged = userService.getUser(username);

        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.getDescription()));
    }

    private static boolean sameUser(UserService userService, ObjectMapper objectMapper, HttpServletRequest request) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userLogged = userService.getUser(username);

        Object requestPathVariable = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        HashMap<String, String> pathVariable = objectMapper.convertValue(requestPathVariable, HashMap.class);
        UserDTO userInfo = userService.getUser(Long.parseLong(pathVariable.get("id")));

        return userLogged.getUsername().equals(userInfo.getUsername());

    }

    private static boolean adminUser(UserService userService, ObjectMapper objectMapper, HttpServletRequest request) {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDTO userLogged = userService.getUser(username);

        return userLogged.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.getDescription()));
    }

    private static Boolean openAccess(UserService userService, ObjectMapper objectMapper, HttpServletRequest request) {
        return true;
    }

    public abstract Boolean hasAccess(
            UserService userService,
            ObjectMapper objectMapper,
            HttpServletRequest request);

}
