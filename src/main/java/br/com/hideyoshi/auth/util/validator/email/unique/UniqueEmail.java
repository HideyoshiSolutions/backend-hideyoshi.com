package br.com.hideyoshi.auth.util.validator.email.unique;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailUnique.class)
@Documented
public @interface UniqueEmail {

    String message() default "Email taken, please choose another";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
