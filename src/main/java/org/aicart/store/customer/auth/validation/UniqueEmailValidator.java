package org.aicart.store.customer.auth.validation;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.auth.dto.CustomerRegistrationDTO;
import org.aicart.store.customer.entity.Customer;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, CustomerRegistrationDTO> {
    @Inject
    ShopContext shopContext;

    @Override
    public boolean isValid(CustomerRegistrationDTO dto, ConstraintValidatorContext context) {
        if (dto.email == null || dto.email.isBlank()) {
            return true;
        }

        if (shopContext.getShopId() == 0) {
            return false;
        }

        return Customer.find("email = ?1 and shop.id = ?2", dto.email, shopContext.getShopId()).firstResultOptional().isEmpty();
    }
}
