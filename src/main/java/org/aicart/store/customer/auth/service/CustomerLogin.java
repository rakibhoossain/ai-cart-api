package org.aicart.store.customer.auth.service;

import io.quarkus.security.Authenticated;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.aicart.authentication.dto.ChangePasswordDTO;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.authentication.dto.OauthLoginDTO;
import org.aicart.authentication.AuthenticationService;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.*;


@ApplicationScoped
public class CustomerLogin extends AuthenticationService {

    @Inject
    JsonWebToken jwt;

    @Context
    UriInfo uriInfo;

    @Inject
    ShopContext shopContext;

    @Override
    @Transactional
    protected Response jwtResponse(LoginCredentialDTO loginCredentialDTO) {
        Customer customer = Customer.find("email = ?1 AND shop.id = ?2", loginCredentialDTO.getEmail(), shopContext.getShopId()).firstResult();
        if(isInvalidCredentials(loginCredentialDTO.getPassword(), customer)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid email or password"))
                    .build();
        }

        customer.lastLoginAt = LocalDateTime.now();
        customer.persist();
        return generateJwtResponse(customer);
    }

    @Override
    @Transactional
    protected Response generateOauthToken(OauthLoginDTO oauthLoginDTO) {
        long now = System.currentTimeMillis() / 1000L;

        // Find the user by email
        Customer dbCustomer = Customer.find("email = ?1 AND shop.id = ?2", oauthLoginDTO.getEmail(), shopContext.getShopId()).firstResult();

        if(dbCustomer != null) {
            dbCustomer.lastLoginAt = LocalDateTime.now();
            if(dbCustomer.verifiedAt == 0) {
                dbCustomer.verifiedAt = now;
            }
            dbCustomer.persist();
            return generateJwtResponse(dbCustomer);
        }

        // Create and persist the new customer
        Customer customer = new Customer();
        customer.firstName = oauthLoginDTO.getName();
        customer.email = oauthLoginDTO.getEmail();
        customer.shop = Shop.findById(shopContext.getShopId());
        customer.verifiedAt = now;
        customer.lastLoginAt = LocalDateTime.now();
        customer.persist();

        return generateJwtResponse(customer);
    }


    private Response generateJwtResponse(Customer entity) {
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 60L * 60 * 24 * 7; // 7 days validity (shorter than before)

        String entityIdentifier = entity.getIdentifier();

        // Build the JWT token with proper issuer that matches configuration
        String token = Jwt.issuer("https://aicart.store")
                .subject(entity.id.toString())
                .upn(entity.email)
                .groups(entityIdentifier)
                .expiresAt(exp)
                .issuedAt(now)
                .sign();

        // Build the response object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", entity.id);
        response.put("email", entity.email);
        response.put("name", (entity.firstName + " " + entity.lastName).trim());
        response.put("verifiedAt", now);

        // Return the token
        return Response.ok(response).build();
    }


    @Override
    @Transactional
    @Authenticated
    public Response changePassword(ChangePasswordDTO changePasswordDTO) {

        String subject = jwt.getSubject();

        // Find the customer by id
        Customer customer = Customer.find("id = ?1 AND shop.id = ?2", subject, shopContext.getShopId()).firstResult();

        if (isInvalidCredentials(changePasswordDTO.getPassword(), customer)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Current password not matched"))
                    .build();
        }

        customer.password = generatePasswordHash(changePasswordDTO.getPassword()); // Ensure your `User` entity has this field
        customer.persist();

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password changed successfully"))
                .build();
    }
}
