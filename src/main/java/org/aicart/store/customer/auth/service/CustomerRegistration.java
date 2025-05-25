package org.aicart.store.customer.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.auth.dto.CustomerRegistrationDTO;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.user.entity.Shop;

import java.util.Map;

@ApplicationScoped
public class CustomerRegistration {

    @Inject
    CustomerEmailVerification customerEmailVerification;

    @Inject
    ShopContext shopContext;

    @Transactional
    public Response register(CustomerRegistrationDTO registrationDTO, String origin) {
        if (registrationDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        // Create and persist the new user
        Customer customer = new Customer();
        customer.email = registrationDTO.email;
        customer.firstName = registrationDTO.firstName;
        customer.lastName = registrationDTO.lastName;
        customer.phone = registrationDTO.phone;
        customer.shop = (Shop) Shop.findByIdOptional(shopContext.getShopId())
                .orElseThrow(() -> new NotFoundException("Shop not found"));
        customer.password = BcryptUtil.bcryptHash(registrationDTO.password);
        customer.persist();

        customerEmailVerification.sendMail(customer, origin);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message", "User registered successfully"))
                .build();
    }
}
