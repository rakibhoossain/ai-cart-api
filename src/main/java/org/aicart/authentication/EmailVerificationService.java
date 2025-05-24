package org.aicart.authentication;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.TokenUser;
import org.aicart.authentication.entity.EmailVerification;
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class EmailVerificationService {

    @Inject
    JsonWebToken jwt;

    @Inject
    Mailer mailer;

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/verify-email.html")
    Template verifyMailTemplate;

    public String generateOtp() {
        final int OTP_LENGTH = 6;

        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(ThreadLocalRandom.current().nextInt(10));
        }
        return otp.toString();
    }


    private void storeToken(long entityId, String identifierName, String otp, long expiredAt) {
        EmailVerification emailVerification = EmailVerification.find("entityId = ?1 AND identifierName = ?2", entityId, identifierName).firstResult();

        if (emailVerification == null) {
            emailVerification = new EmailVerification();
            emailVerification.entityId = entityId;
            emailVerification.identifierName = identifierName;
        }

        emailVerification.token = otp;
        emailVerification.expiredAt = expiredAt;
        emailVerification.persist();
    }


    public void sendMail(User user, String origin) {

        final long expiryDuration = 10 * 60L;
        long expiredAt = System.currentTimeMillis() / 1000L + expiryDuration;

        String token = TokenGenerator.generateToken(user.id, user.email, expiredAt);
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

//         mailer.send(mail);

    }

    @Authenticated
    @Transactional
    public Response emailVerifyCode(String token) {

        long currentTime = System.currentTimeMillis() / 1000;

        EmailVerification emailVerification = EmailVerification.find("userId = ?1 and token = ?2 AND expiredAt >= ?3", jwt.getSubject(), token, currentTime)
                .firstResult();


        if (emailVerification == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        User user = User.findById(emailVerification.entityId);

        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }
        
        user.verifiedAt = currentTime;
        user.persist();

        EmailVerification.delete("userId = ?1", emailVerification.entityId);


        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }


    @Transactional
    public Response emailVerifyToken(String token) {

        TokenUser tokenUser = TokenGenerator.getTokenUser(token);
        if(tokenUser == null) {
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

        if(user.verifiedAt > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "User already verified"))
                    .build();
        }


        user.verifiedAt = System.currentTimeMillis() / 1000;
        user.persist();

        EmailVerification.delete("userId = ?1", user.id);


        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Email verified successfully"))
                .build();
    }

}
