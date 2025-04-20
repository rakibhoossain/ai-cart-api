package org.aicart.store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.product.ProductCollectionFieldEnum;
import org.aicart.store.product.dto.ProductCollectionConditionDTO;

public class ConditionValidator implements ConstraintValidator<ValidCondition, ProductCollectionConditionDTO> {

    @Override
    public boolean isValid(ProductCollectionConditionDTO condition, ConstraintValidatorContext context) {
        if (condition.field == null || condition.value == null) {
            return true; // Let other validators handle null checks
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (condition.field.equals(ProductCollectionFieldEnum.PRICE)) {
            if (!(condition.value instanceof Number)) {
                context.buildConstraintViolationWithTemplate("Price must be a number")
                        .addPropertyNode("value")
                        .addConstraintViolation();
                isValid = false;
            }
        } else if(condition.field.equals(ProductCollectionFieldEnum.TAG) || condition.field.equals(ProductCollectionFieldEnum.CATEGORY)) {
            if (!(condition.value instanceof Number)) {
                context.buildConstraintViolationWithTemplate("Value must be int")
                .addPropertyNode("value")
                .addConstraintViolation();
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
