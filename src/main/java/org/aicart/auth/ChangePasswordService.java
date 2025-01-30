package org.aicart.auth;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.aicart.store.user.entity.User;

import java.util.Map;

@ApplicationScoped
@Authenticated
public class ChangePasswordService {

    @Inject
    JsonWebToken jwt;

    @Transactional
    public Response changePassword(String oldPassword, String newPassword) {

        String subject = jwt.getSubject();

        // Find the user by id
        User user = User.find("id", subject).firstResult();

        if (user == null || !BcryptUtil.matches(oldPassword, user.password)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Current password not matched"))
                    .build();
        }

        user.password = BcryptUtil.bcryptHash(newPassword); // Ensure your `User` entity has this field
        user.persist();

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password changed successfully"))
                .build();
    }
}
