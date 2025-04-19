package org.aicart.store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.product.ProductCollectionTypeEnum;
import org.aicart.store.product.dto.ProductCollectionDTO;

public class CollectionTypeValidator implements ConstraintValidator<ValidCollectionType, ProductCollectionDTO> {
    @Override
    public boolean isValid(ProductCollectionDTO dto, ConstraintValidatorContext context) {
        if (dto.collectionType == null) {
            return true; // @NotNull validation will handle this
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (dto.collectionType == ProductCollectionTypeEnum.MANUAL) {
            if (dto.productIds == null || dto.productIds.isEmpty()) {
                context.buildConstraintViolationWithTemplate("Products are required for manual collections")
                        .addPropertyNode("products")
                        .addConstraintViolation();
                isValid = false;
            }
        } else { // SMART collection
            if (dto.conditionMatch == null) {
                context.buildConstraintViolationWithTemplate("Match type is required for smart collections")
                        .addPropertyNode("match")
                        .addConstraintViolation();
                isValid = false;
            }

            if (dto.conditions == null || dto.conditions.isEmpty()) {
                context.buildConstraintViolationWithTemplate("At least one condition is required for smart collections")
                        .addPropertyNode("conditions")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
