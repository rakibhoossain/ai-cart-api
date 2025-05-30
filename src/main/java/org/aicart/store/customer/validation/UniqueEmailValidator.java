package org.aicart.store.customer.validation;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.entity.Customer;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, CustomerDTO> {
    @Inject
    ShopContext shopContext;

    @Override
    public boolean isValid(CustomerDTO dto, ConstraintValidatorContext context) {
        if (dto.email == null || dto.email.isBlank()) {
            return true;
        }

        if (shopContext.getShopId() == 0) {
            return false;
        }

        return Customer.find("email = ?1 and shop.id = ?2", dto.email, shopContext.getShopId()).firstResultOptional().isEmpty();
    }
}
