package org.aicart.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.auth.EmailVerifyService;
import org.aicart.auth.dto.RegistrationDTO;
import store.aicart.user.entity.User;

import java.util.Map;

@ApplicationScoped
public class UserRegistration {

    @Inject
    EmailVerifyService emailVerifyService;

    @Transactional
    public Response register(RegistrationDTO registrationDTO) {
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

        emailVerifyService.sendMail(user);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message", "User registered successfully"))
                .build();
    }
}
