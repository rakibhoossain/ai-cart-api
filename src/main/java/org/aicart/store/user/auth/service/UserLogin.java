package org.aicart.store.user.auth.service;

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
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserLogin extends AuthenticationService {
    @Inject
    JsonWebToken jwt;

    @Context
    UriInfo uriInfo; // Provides request context

    @Override
    @Transactional
    protected Response jwtResponse(LoginCredentialDTO loginCredentialDTO) {
        User user = User.find("email", loginCredentialDTO.getEmail()).firstResult();
        if(isInvalidCredentials(loginCredentialDTO.getPassword(), user)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid email or password"))
                    .build();
        }

        user.lastLoginAt = LocalDateTime.now();
        user.persist();

        return generateJwtResponse(user);
    }


    @Transactional
    @Override
    public Response generateOauthToken(OauthLoginDTO oauthLoginDTO) {

        long now = System.currentTimeMillis() / 1000L;

        // Find the user by email
        User dbUser = User.find("email", oauthLoginDTO.getEmail()).firstResult();

        if(dbUser != null) {
            dbUser.lastLoginAt = LocalDateTime.now();
            if(dbUser.verifiedAt == 0) {
                dbUser.verifiedAt = now;
            }
            dbUser.persist();
            return generateJwtResponse(dbUser);
        }

        // Create and persist the new user
        User user = new User();
        user.name = oauthLoginDTO.getName();
        user.email = oauthLoginDTO.getEmail();
        user.verifiedAt = now;
        user.lastLoginAt = LocalDateTime.now();
        user.persist();

        return generateJwtResponse(user);
    }

    private Response generateJwtResponse(User entity){
        long now = System.currentTimeMillis() / 1000L;
        long exp = now + 60L * 60 * 24 * 7; // 7 days validity (consistent with CustomerLogin)

        String entityIdentifier = entity.getIdentifier();

        // Build the JWT token with proper issuer that matches configuration
        String token = Jwt.issuer("https://aicart.store")
                .subject(entity.id.toString())
                .upn(entity.email)
                .groups(entityIdentifier) // Use the dynamic entityIdentifier for groups
                .expiresAt(exp)
                .issuedAt(now)
                .sign();

        // Build the response object
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", entity.id);
        response.put("email", entity.email);
        response.put("name", entity.name);
        response.put("verifiedAt", entity.verifiedAt);

        // Return the token
        return Response.ok(response).build();
    }

    @Override
    @Transactional
    @Authenticated
    public Response changePassword(ChangePasswordDTO changePasswordDTO) {
        String subject = jwt.getSubject();

        // Find the user by id
        User user = User.find("id", subject).firstResult();

        if (isInvalidCredentials(changePasswordDTO.getPassword(), user)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Current password not matched"))
                    .build();
        }

        user.password = generatePasswordHash(changePasswordDTO.getPassword()); // Ensure your `User` entity has this field
        user.persist();

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password changed successfully"))
                .build();
    }
}
