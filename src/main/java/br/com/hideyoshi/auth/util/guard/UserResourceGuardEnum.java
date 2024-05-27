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

    USER("user"),

    SAME_USER("same_user"),

    ADMIN_USER("admin_user"),

    OPEN("open");

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

}
