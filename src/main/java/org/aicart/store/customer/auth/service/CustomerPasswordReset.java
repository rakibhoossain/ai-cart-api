package org.aicart.store.customer.auth.service;

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
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;

import java.util.Map;

@ApplicationScoped
public class CustomerPasswordReset extends PasswordResetService {

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/reset-password.html")
    Template resetPasswordMailTemplate;

    @Context
    SecurityContext securityContext;

    @Inject
    ShopContext shopContext;

    @Transactional
    public Response forgetPassword(String email, String origin) {

        // Check if the user is authenticated
        if (securityContext.getUserPrincipal() != null) {
            // If authenticated, block access to this route
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", "Authenticated users cannot reset their password."))
                    .build();
        }

        Customer customer = Customer.find("where email = ?1 AND shop.id = ?2", email, shopContext.getShopId()).firstResult();

        if(customer == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Invalid email address.")).build();
        }

        sendMail(customer, origin);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Reset link sent successfully"))
                .build();
    }


    private void sendMail(Customer customer, String origin) {

        long expiredAt = getExpiryDuration();

        String token = TokenGenerator.generateToken(customer.id, customer.getIdentifier(), customer.email, expiredAt);

        TemplateInstance resetPasswordMailInstance = resetPasswordMailTemplate
                .data("origin", origin)
                .data("name", customer.firstName)
                .data("token", token)
                .data("expiryMinutes", 10);

        System.out.println("SEND EMAIL VERIFY SERVICE");

        Mail mail = Mail.withHtml(customer.email,
                "Test Email reset password Quarkus",
                resetPasswordMailInstance.render());


        storeToken(customer.id, customer.getIdentifier(), token, expiredAt);

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

        Customer customer = Customer.findById(tokenUser.getUserId());
        if(customer == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Link expired"))
                    .build();
        }

        customer.password = BcryptUtil.bcryptHash(resetPasswordDTO.getPassword());
        customer.persist();

        PasswordReset.delete("entityId = ?1 AND identifierName = ?2", customer.id, customer.getIdentifier());

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password reset successfully"))
                .build();
    }
}
