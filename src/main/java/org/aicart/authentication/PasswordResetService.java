package org.aicart.authentication;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.aicart.authentication.dto.ResetPasswordDTO;
import org.aicart.authentication.dto.TokenUser;
import org.aicart.authentication.entity.PasswordReset;
import org.aicart.store.user.entity.User;

import java.util.Map;

public abstract class PasswordResetService {

    protected void storeToken(long entityId, String identifierName, String token, long expiredAt) {
        PasswordReset passwordReset = PasswordReset.find("entityId = ?1 AND identifierName = ?2", entityId, identifierName).firstResult();

        if (passwordReset == null) {
            passwordReset = new PasswordReset();
            passwordReset.entityId = entityId;
            passwordReset.identifierName = identifierName;
        }

        passwordReset.token = token;
        passwordReset.expiredAt = expiredAt;
        passwordReset.persist();
    }

    protected long getExpiryDuration() {
        final long expiryDuration = 10 * 60L;
        return System.currentTimeMillis() / 1000L + expiryDuration;
    }
}
