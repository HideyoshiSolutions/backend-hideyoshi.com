package com.hideyoshi.backendportfolio.util.validator.password;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        return Pattern.compile(PASSWORD_PATTERN)
                .matcher(password)
                .matches();

    }
}
