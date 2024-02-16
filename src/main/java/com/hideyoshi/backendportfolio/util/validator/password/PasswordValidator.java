package com.hideyoshi.backendportfolio.util.validator.password;

import com.hideyoshi.backendportfolio.base.user.entity.Provider;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    Provider provider;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.provider = constraintAnnotation.provider();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (this.provider.equals(Provider.GOOGLE)) {
            return true;
        } else {
            return Pattern.compile(PASSWORD_PATTERN)
                    .matcher(password)
                    .matches();
        }

    }
}
