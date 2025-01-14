package org.aicart.auth.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import store.aicart.user.entity.User;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true; // Let other validators handle null/blank checks
        }

        return User.find("email", email).firstResultOptional().isEmpty();
    }
}
