package org.aicart.store.customer.auth.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.EmailVerificationService;
import org.aicart.authentication.TokenGenerator;
import org.aicart.authentication.dto.TokenUser;
import org.aicart.authentication.entity.EmailVerification;
import org.aicart.store.customer.entity.Customer;

import java.util.Map;

@ApplicationScoped
public class CustomerEmailVerification extends EmailVerificationService {

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/verify-email.html")
    Template verifyMailTemplate;

    public void sendMail(Customer customer, String origin) {

        long expiredAt = getExpiryDuration();

        String token = TokenGenerator.generateToken(customer.id, customer.getIdentifier(), customer.email, expiredAt);
        String otp = generateOtp();

        TemplateInstance verifyMailInstance = verifyMailTemplate
                .data("origin", origin)
                .data("name", customer.firstName)
                .data("code", otp)
                .data("token", token)
                .data("expiryMinutes", 10);

        System.out.println("SEND EMAIL VERIFY SERVICE");

        Mail mail = Mail.withHtml(customer.email,
                "Test Email from Quarkus",
                verifyMailInstance.render());

        storeToken(customer.id, customer.getIdentifier(), otp, expiredAt);

        reactiveMailer.send(mail).subscribe().with(
                success -> System.out.println("Email sent successfully!"),
                failure -> System.err.println("Failed to send email: " + failure.getMessage())
        );
    }

    @Override
    @Transactional
    protected Response verifyEmailVerificationEntity(EmailVerification emailVerification) {
        Customer customer = Customer.findById(emailVerification.entityId);
        if(customer == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        if(customer.verifiedAt > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "User already verified"))
                    .build();
        }

        customer.verifiedAt = System.currentTimeMillis() / 1000;
        customer.persist();
        EmailVerification.delete("entityId = ?1 AND identifierName = ?2", emailVerification.entityId, emailVerification.identifierName);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }

    @Override
    @Transactional
    protected Response verifyTokenUserEntity(TokenUser tokenUser) {

        if(tokenUser == null || !tokenUser.getIdentifierName().equals("customer")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        Customer customer = Customer.findById(tokenUser.getUserId());
        if(customer == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        customer.verifiedAt = System.currentTimeMillis() / 1000;
        customer.persist();

        EmailVerification.delete("entityId = ?1 AND identifierName = ?2", customer.id, tokenUser.getIdentifierName());

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }
}
