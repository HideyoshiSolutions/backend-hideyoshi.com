package com.hideyoshi.backendportfolio.util.validator.password;

import com.hideyoshi.backendportfolio.base.auth.entity.Provider;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default
            "The password must have at least: a special character, a number, a uppercase and a lowercase ";

    Provider provider() default Provider.LOCAL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
