package org.aicart.store.user.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.store.user.auth.dto.RegistrationDTO;
import org.aicart.store.user.entity.User;

import java.util.Map;

@ApplicationScoped
public class UserRegistration {

    @Inject
    UserEmailVerification userEmailVerification;

    @Transactional
    public Response register(RegistrationDTO registrationDTO, String origin) {
        if (registrationDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        String hashedPassword = BcryptUtil.bcryptHash(registrationDTO.getPassword());

        // Create and persist the new user
        User user = new User();
        user.name = registrationDTO.getName();
        user.email = registrationDTO.getEmail();
        user.password = hashedPassword;
        user.persist();

        userEmailVerification.sendMail(user, origin);

        return Response.status(Response.Status.CREATED)
                .entity(Map.of("message", "User registered successfully"))
                .build();
    }
}
