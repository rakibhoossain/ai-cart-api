package org.aicart.auth.service;

import org.aicart.auth.dto.TokenUser;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class TokenGenerator {

    private static final String SECRET_KEY = "A3j17B5W6qC2X4r8F9nV0ZtYdU3eS7I2L5H4K9";

    public static String generateToken(long userId, String email, long expiredAt) {
        String payload = userId + ":" + email + ":" + expiredAt;

        String signature = hmacSha256(payload);
        return Base64.getUrlEncoder().withoutPadding().encodeToString((payload + ":" + signature).getBytes());
    }

    public static TokenUser getTokenUser(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token));
            String[] parts = decoded.split(":");

            if (parts.length != 4) throw new IllegalArgumentException("Invalid token");

            long userId = Long.parseLong(parts[0]);
            String email = parts[1];
            long expiredAt = Long.parseLong(parts[2]);
            String signature = parts[3];

            if (expiredAt < System.currentTimeMillis() / 1000L) {
                throw new RuntimeException("Token has expired");
            }

            String expectedSignature = hmacSha256(String.join(":", parts[0], parts[1], parts[2]));

            if (!signature.equals(expectedSignature)) {
                throw new RuntimeException("Invalid signature");
            }

            return new TokenUser(userId, email);

        } catch (Exception e) {
            return null; // Invalid token
        }
    }


    private static String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC", e);
        }
    }
}