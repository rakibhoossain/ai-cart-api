package org.aicart.authentication;


import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.TokenUser;
import org.aicart.authentication.entity.EmailVerification;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EmailVerificationService {

    @Inject
    JsonWebToken jwt;


    public String generateOtp() {
        final int OTP_LENGTH = 6;

        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(ThreadLocalRandom.current().nextInt(10));
        }
        return otp.toString();
    }

    protected void storeToken(long entityId, String identifierName, String otp, long expiredAt) {
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

    public long getExpiryDuration() {
        final long expiryDuration = 10 * 60L;
        return System.currentTimeMillis() / 1000L + expiryDuration;
    }

    protected abstract Response verifyEmailVerificationEntity(EmailVerification emailVerification);
    protected abstract Response verifyTokenUserEntity(TokenUser tokenUser);

    protected EmailVerification getEmailVerificationEntityByCode(String token) {
        long currentTime = System.currentTimeMillis() / 1000;

        return EmailVerification.find("userId = ?1 and token = ?2 AND expiredAt >= ?3", jwt.getSubject(), token, currentTime)
                .firstResult();
    }

    public Response emailVerifyCode(String token) {
        EmailVerification emailVerification = getEmailVerificationEntityByCode(token);
        if (emailVerification == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }
        return verifyEmailVerificationEntity(emailVerification);
    }


    public Response emailVerifyToken(String token) {

        TokenUser tokenUser = TokenGenerator.getTokenUser(token);
        if(tokenUser == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Code expired"))
                    .build();
        }

        return verifyTokenUserEntity(tokenUser);
    }

}
