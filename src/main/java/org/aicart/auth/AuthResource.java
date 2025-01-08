package org.aicart.auth;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import java.util.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.jwt.Claims;
import io.smallrye.jwt.build.Jwt;
import org.aicart.auth.dto.LoginCredentialDTO;
import org.jboss.resteasy.reactive.NoCache;
import org.aicart.auth.dto.RegistrationDTO;
import store.aicart.user.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Context
    UriInfo uriInfo; // Provides request context

    @POST
    @Path("/login")
    public Response login(@Valid LoginCredentialDTO loginCredentialDTO) {

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
                .claim("email_verified", false)
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

        // Return the token
        return Response.ok(response).build();
    }


    @POST
    @Path("/register")
    @NoCache
    @Transactional
    public Response register(@Valid RegistrationDTO registrationDTO) {
        if (registrationDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Request body is required"))
                    .build();
        }

        String hashedPassword = BcryptUtil.bcryptHash(registrationDTO.getPassword());

        // Check if the email already exists
        if (User.find("email", registrationDTO.getEmail()).firstResultOptional().isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Email is already registered"))
                    .build();
        }

        // Create and persist the new user
        User user = new User();
        user.name = registrationDTO.getName();
        user.email = registrationDTO.getEmail();
        user.password = hashedPassword; // Ensure your `User` entity has this field
        user.persist();

        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message", "User registered successfully"))
                .build();
    }
}

