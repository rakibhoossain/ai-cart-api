package org.aicart.store.user.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.aicart.authentication.PasswordResetService;
import org.aicart.authentication.TokenGenerator;
import org.aicart.authentication.dto.ResetPasswordDTO;
import org.aicart.authentication.dto.TokenUser;
import org.aicart.authentication.entity.PasswordReset;
import org.aicart.store.user.entity.User;

import java.util.Map;

@ApplicationScoped
public class UserPasswordReset extends PasswordResetService {

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/reset-password.html")
    Template resetPasswordMailTemplate;

    @Context
    SecurityContext securityContext;

    @Transactional
    public Response forgetPassword(String email, String origin) {

        System.out.println("origin: "+ origin);

        // Check if the user is authenticated
        if (securityContext.getUserPrincipal() != null) {
            // If authenticated, block access to this route
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", "Authenticated users cannot reset their password."))
                    .build();
        }

        User user = User.find("where email = ?1", email).firstResult();

        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Invalid email address.")).build();
        }

        sendMail(user, origin);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Reset link sent successfully"))
                .build();
    }


    private void sendMail(User user, String origin) {

        long expiredAt = getExpiryDuration();

        String token = TokenGenerator.generateToken(user.id, user.getIdentifier(), user.email, expiredAt);

        TemplateInstance resetPasswordMailInstance = resetPasswordMailTemplate
                .data("origin", origin)
                .data("name", user.name)
                .data("token", token)
                .data("expiryMinutes", 10);

        System.out.println("SEND EMAIL VERIFY SERVICE");

        Mail mail = Mail.withHtml(user.email,
                "Test Email reset password Quarkus",
                resetPasswordMailInstance.render());


        storeToken(user.id, user.getIdentifier(), token, expiredAt);

        reactiveMailer.send(mail).subscribe().with(
                success -> System.out.println("Email sent successfully!"),
                failure -> System.err.println("Failed to send email: " + failure.getMessage())
        );

    }

    @Transactional
    public Response resetPassword(ResetPasswordDTO resetPasswordDTO) {

        // Check if the user is authenticated
        if (securityContext.getUserPrincipal() != null) {
            // If authenticated, block access to this route
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", "Authenticated users cannot reset their password."))
                    .build();
        }

        long currentTime = System.currentTimeMillis() / 1000;

        TokenUser tokenUser = TokenGenerator.getTokenUser(resetPasswordDTO.getToken());
        if(tokenUser == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Link expired"))
                    .build();
        }

        PasswordReset passwordReset =
                PasswordReset.find("entityId = ?1 AND identifierName = ?2 AND token = ?3 AND expiredAt >= ?4", tokenUser.getUserId(), tokenUser.getIdentifierName(), resetPasswordDTO.getToken(), currentTime)
                        .firstResult();

        if (passwordReset == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Link expired"))
                    .build();
        }

        User user = User.findById(tokenUser.getUserId());
        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Link expired"))
                    .build();
        }

        user.password = BcryptUtil.bcryptHash(resetPasswordDTO.getPassword());
        user.persist();

        PasswordReset.delete("entityId = ?1 AND identifierName = ?2", user.id, user.getIdentifier());

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password reset successfully"))
                .build();
    }
}
