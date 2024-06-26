package br.com.hideyoshi.auth.util.validator.email.unique;

import br.com.hideyoshi.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class EmailUnique implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        AtomicReference<Boolean> emailValid = new AtomicReference();
        this.userRepository.findByEmail(email).ifPresentOrElse(
                (value) -> {
                    emailValid.set(false);
                },
                () -> {
                    emailValid.set(true);
                }
        );

        return emailValid.get();
    }
}
