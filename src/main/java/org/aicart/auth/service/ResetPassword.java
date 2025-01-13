package org.aicart.auth.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.aicart.auth.dto.ResetPasswordDTO;
import org.aicart.auth.dto.TokenUser;
import org.aicart.auth.entity.PasswordReset;
import store.aicart.user.entity.User;

import java.util.Map;

@ApplicationScoped
public class ResetPassword {

    @Inject
    ReactiveMailer reactiveMailer;

    @Inject
    @Location("mail/reset-password.html")
    Template resetPasswordMailTemplate;


    private void storeToken(User user, String token, long expiredAt) {
        PasswordReset passwordReset = PasswordReset.find("userId", user.id).firstResult();

        if (passwordReset == null) {
            passwordReset = new PasswordReset();
            passwordReset.userId = user.id;
        }

        passwordReset.token = token;
        passwordReset.expiredAt = expiredAt;
        passwordReset.persist();
    }

    @Transactional
    public Response resetPassword(String email) {
        User user = User.find("where email = ?1", email).firstResult();

        if(user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Invalid email address.")).build();
        }

        sendMail(user);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Reset link sent successfully"))
                .build();
    }


    private void sendMail(User user) {

        final long expiryDuration = 10 * 60L;
        long expiredAt = System.currentTimeMillis() / 1000L + expiryDuration;

        String token = TokenGenerator.generateToken(user.id, user.email, expiredAt);

        TemplateInstance resetPasswordMailInstance = resetPasswordMailTemplate.data("name", user.name)
                .data("token", token)
                .data("expiryMinutes", 10);

        System.out.println("SEND EMAIL VERIFY SERVICE");

        Mail mail = Mail.withHtml(user.email,
                "Test Email reset password Quarkus",
                resetPasswordMailInstance.render());


        storeToken(user, token, expiredAt);

        reactiveMailer.send(mail).subscribe().with(
                success -> System.out.println("Email sent successfully!"),
                failure -> System.err.println("Failed to send email: " + failure.getMessage())
        );

//         mailer.send(mail);

    }

    @Transactional
    public Response updatePassword(ResetPasswordDTO resetPasswordDTO) {

        long currentTime = System.currentTimeMillis() / 1000;

        TokenUser tokenUser = TokenGenerator.getTokenUser(resetPasswordDTO.getToken());
        if(tokenUser == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Link expired"))
                    .build();
        }

        PasswordReset passwordReset = PasswordReset.find("userId = ?1 and token = ?2 AND expiredAt >= ?3", tokenUser.getUserId(), resetPasswordDTO.getToken(), currentTime)
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

        PasswordReset.delete("userId = ?1", user.id);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Password reset successfully"))
                .build();
    }
}
