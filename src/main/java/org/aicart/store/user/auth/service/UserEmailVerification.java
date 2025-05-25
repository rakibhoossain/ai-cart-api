package org.aicart.store.user.auth.service;

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
import org.aicart.store.user.entity.User;

import java.util.Map;

@ApplicationScoped
public class UserEmailVerification extends EmailVerificationService {

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/verify-email.html")
    Template verifyMailTemplate;

    public void sendMail(User user, String origin) {

        long expiredAt = getExpiryDuration();

        String token = TokenGenerator.generateToken(user.id, user.getIdentifier(), user.email, expiredAt);
        String otp = generateOtp();

        TemplateInstance verifyMailInstance = verifyMailTemplate
                .data("origin", origin)
                .data("name", user.name)
                .data("code", otp)
                .data("token", token)
                .data("expiryMinutes", 10);

        System.out.println("SEND EMAIL VERIFY SERVICE");

        Mail mail = Mail.withHtml(user.email,
                "Test Email from Quarkus",
                verifyMailInstance.render());

        storeToken(user.id, user.getIdentifier(), otp, expiredAt);

        reactiveMailer.send(mail).subscribe().with(
                success -> System.out.println("Email sent successfully!"),
                failure -> System.err.println("Failed to send email: " + failure.getMessage())
        );
    }

    @Override
    @Transactional
    protected Response verifyEmailVerificationEntity(EmailVerification emailVerification) {
        User user = User.findById(emailVerification.entityId);
        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        if(user.verifiedAt > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "User already verified"))
                    .build();
        }

        user.verifiedAt = System.currentTimeMillis() / 1000;
        user.persist();
        EmailVerification.delete("entityId = ?1 AND identifierName = ?2", emailVerification.entityId, emailVerification.identifierName);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }

    @Override
    @Transactional
    protected Response verifyTokenUserEntity(TokenUser tokenUser) {

        if(tokenUser == null || !tokenUser.getIdentifierName().equals("user")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        User user = User.findById(tokenUser.getUserId());
        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        user.verifiedAt = System.currentTimeMillis() / 1000;
        user.persist();

        EmailVerification.delete("entityId = ?1 AND identifierName = ?2", user.id, tokenUser.getIdentifierName());

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }
}
