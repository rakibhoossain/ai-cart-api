package org.aicart.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.aicart.auth.dto.LoginCredentialDTO;
import org.eclipse.microprofile.jwt.Claims;
import store.aicart.user.entity.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserLogin {

    @Context
    UriInfo uriInfo; // Provides request context

    public Response login(LoginCredentialDTO loginCredentialDTO) {

        if (loginCredentialDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Request body is required"))
                    .build();
        }

        // Find the user by email
        User user = User.find("email", loginCredentialDTO.getEmail()).firstResult();

        if (user == null || !BcryptUtil.matches(loginCredentialDTO.getPassword(), user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid email or password"))
                    .build();
        }

        // Define roles
        List<String> realmRoles = Arrays.asList("offline_access", "default-roles-aicart", "uma_authorization");
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
                .subject(user.id.toString())
                .claim(Claims.exp, Long.MAX_VALUE)
                .claim(Claims.iat, System.currentTimeMillis() / 1000)
                .claim(Claims.auth_time, System.currentTimeMillis() / 1000)
                .claim("typ", "Bearer") // Custom claim for type
                .claim("allowed_origins", List.of("*"))
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .claim("scope", "openid profile email") // Custom claim for scope
                .claim("email_verified", user.verifiedAt > 0)
                .claim("name", user.name)
                .claim("preferred_username", user.name)
                .claim("given_name", user.name)
                .claim("family_name", user.name)
                .claim("email", user.email)
                .sign();

        // Build the response object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.id);
        response.put("email", user.email);
        response.put("name", user.name);
        response.put("verifiedAt", user.verifiedAt);

        // Return the token
        return Response.ok(response).build();
    }

}
