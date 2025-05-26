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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

        // Find the user by email
        Customer dbCustomer = Customer.find("email = ?1 AND shop.id = ?2", oauthLoginDTO.getEmail(), shopContext.getShopId()).firstResult();

        if(dbCustomer != null) {
            dbCustomer.lastLoginAt = LocalDateTime.now();
            if(dbCustomer.verifiedAt == 0) {
                dbCustomer.verifiedAt = System.currentTimeMillis() / 1000L;
            }
            dbCustomer.persist();
            return generateJwtResponse(dbCustomer);
        }

        // Create and persist the new customer
        Customer customer = new Customer();
        customer.firstName = oauthLoginDTO.getName();
        customer.email = oauthLoginDTO.getEmail();
        customer.shop = Shop.findById(shopContext.getShopId());
        customer.verifiedAt = System.currentTimeMillis() / 1000;
        customer.lastLoginAt = LocalDateTime.now();
        customer.persist();

        return generateJwtResponse(customer);
    }


    private Response generateJwtResponse(Customer entity){

        String entityIdentifier = entity.getIdentifier();

        // Define roles
        List<String> realmRoles = Arrays.asList(entityIdentifier, "offline_access", "default-roles-aicart", "uma_authorization");
        List<String> resourceRoles = Arrays.asList("manage-account", "manage-account-links", "view-profile");

        // Prepare `realm_access` claim as a map
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", realmRoles);

        // Prepare `resource_access` claim as a map
        Map<String, Object> resourceAccessRoles = new HashMap<>();
        resourceAccessRoles.put("roles", resourceRoles);
        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("account", resourceAccessRoles);

        // Build the JWT token
        String token = Jwt.issuer(uriInfo.getBaseUri().toString())
                .subject(entity.id.toString())
                .claim(Claims.exp, Long.MAX_VALUE)
                .claim(Claims.iat, System.currentTimeMillis() / 1000)
                .claim(Claims.auth_time, System.currentTimeMillis() / 1000)
                .claim("typ", "Bearer") // Custom claim for type
                .claim("allowed_origins", List.of("*"))
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .claim("scope", "openid profile email") // Custom claim for scope
                .claim("email_verified", true)
                .claim("name", entity.firstName)
                .claim("preferred_username", entity.firstName)
                .claim("given_name", entity.firstName)
                .claim("family_name", entity.firstName)
                .claim("email", entity.email)
                .sign();

        // Build the response object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", entity.id);
        response.put("email", entity.email);
        response.put("name", (entity.firstName + " " + entity.lastName).trim());
        response.put("verifiedAt", System.currentTimeMillis() / 1000);

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
