package org.aicart.store.product.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.product.dto.DiscountDTO;

public class DiscountValidator implements ConstraintValidator<ValidDiscount, DiscountDTO> {

    @Override
    public boolean isValid(DiscountDTO discountDTO, ConstraintValidatorContext context) {
        if (discountDTO == null) {
            return true;
        }

        if(Boolean.TRUE.equals(discountDTO.isAutomatic) && discountDTO.title == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Title is required")
                    .addPropertyNode("title")
                    .addConstraintViolation();
            return false;
        } else if(Boolean.FALSE.equals(discountDTO.isAutomatic) && discountDTO.coupon == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Coupon code is required")
                    .addPropertyNode("coupon")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
