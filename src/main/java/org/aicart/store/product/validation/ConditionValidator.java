package org.aicart.store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.product.dto.ProductCollectionConditionDTO;

public class ConditionValidator implements ConstraintValidator<ValidCondition, ProductCollectionConditionDTO> {

    @Override
    public boolean isValid(ProductCollectionConditionDTO condition, ConstraintValidatorContext context) {
        if (condition.field == null || condition.value == null) {
            return true; // Let other validators handle null checks
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if ("price".equals(condition.field)) {
            if (!(condition.value instanceof Number)) {
                context.buildConstraintViolationWithTemplate("Price must be a number")
                        .addPropertyNode("value")
                        .addConstraintViolation();
                isValid = false;
            }
        } else {
            if (!(condition.value instanceof String) || ((String) condition.value).isEmpty()) {
                context.buildConstraintViolationWithTemplate("Value must be non-empty text")
                        .addPropertyNode("value")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
