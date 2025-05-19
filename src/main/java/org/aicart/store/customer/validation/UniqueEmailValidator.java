package org.aicart.store.customer.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.entity.Customer;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, CustomerDTO> {

    @Override
    public boolean isValid(CustomerDTO dto, ConstraintValidatorContext context) {
        if (dto.email == null || dto.email.isBlank()) {
            return true;
        }

        if (dto.shopId == null) {
            return true;
        }

        return Customer.find("email = ?1 and shop.id = ?2", dto.email, dto.shopId).firstResultOptional().isEmpty();
    }
}
