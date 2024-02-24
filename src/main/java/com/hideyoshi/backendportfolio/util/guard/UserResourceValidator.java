package com.hideyoshi.backendportfolio.util.guard;

import com.hideyoshi.backendportfolio.base.auth.model.UserDTO;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserResourceValidator implements ConstraintValidator<UserResourceGuard, UserDTO> {

    @Override
    public void initialize(UserResourceGuard constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDTO userDTO, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return false;
    }

}
