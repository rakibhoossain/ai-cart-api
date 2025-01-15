package org.aicart.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.aicart.auth.dto.LoginCredentialDTO;
import org.aicart.auth.dto.OauthLoginDTO;
import store.aicart.user.entity.User;
import java.security.SecureRandom;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@ApplicationScoped
public class UserLogin {

    @Context
    UriInfo uriInfo; // Provides request context


    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserLogin() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Response login(LoginCredentialDTO loginCredentialDTO) {

        if (loginCredentialDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        // Find the user by email
        User user = User.find("email", loginCredentialDTO.getEmail()).firstResult();

        if (user == null || !BcryptUtil.matches(loginCredentialDTO.getPassword(), user.password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid email or password"))
                    .build();
        }

        return TokenResponse.build(user, uriInfo);
    }



    public Response oauthLogin(OauthLoginDTO oauthLoginDTO) {
        if (oauthLoginDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        try {
            if ("google".equalsIgnoreCase(oauthLoginDTO.getProvider())) {
                return validateGoogleToken(oauthLoginDTO);
            } else if ("github".equalsIgnoreCase(oauthLoginDTO.getProvider())) {
                return validateGitHubToken(oauthLoginDTO);
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "Request body is required"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }
    }




    private Response validateGoogleToken(OauthLoginDTO oauthLoginDTO) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/oauth2/v2/userinfo"))
                .header("Authorization", "Bearer " + oauthLoginDTO.getAccessToken()) // Add access token to the Authorization header
                .header("User-Agent", "authjs") // Set the User-Agent header
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                if(jsonNode.has("id")) {
                    return generateOauthToken(oauthLoginDTO);
                }
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid Google token"))
                    .build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("message", "Invalid Google token"))
                .build();
    }

    private Response validateGitHubToken(OauthLoginDTO oauthLoginDTO) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user"))
                .header("Authorization", "Bearer " + oauthLoginDTO.getAccessToken()) // Add access token to the Authorization header
                .header("User-Agent", "auths") // Set the User-Agent header
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(response.body());
                if(jsonNode.has("id")) {
                    return generateOauthToken(oauthLoginDTO);
                }
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Invalid GitHub token"))
                    .build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("message", "Invalid GitHub token"))
                .build();
    }

    private String generateStrongPassword(int length) {

        // Define the characters for the password
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

        // Combine all character sets
        String allCharacters = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARACTERS;

        // Use SecureRandom for better randomness
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Ensure the password contains at least one character from each category
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password with random characters
        for (int i = password.length(); i < length; i++) {
            password.append(allCharacters.charAt(random.nextInt(allCharacters.length())));
        }

        // Shuffle the password to randomize character positions
        for (int i = 0; i < password.length(); i++) {
            int j = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }


    @Transactional
    public Response generateOauthToken(OauthLoginDTO oauthLoginDTO) {

        long now = System.currentTimeMillis() / 1000L;

        // Find the user by email
        User dbUser = User.find("email", oauthLoginDTO.getEmail()).firstResult();

        if(dbUser != null) {
            if(dbUser.verifiedAt == 0) {
                dbUser.verifiedAt = now;
                dbUser.persist();
            }
            return TokenResponse.build(dbUser, uriInfo);
        }

        SecureRandom random = new SecureRandom();

        String hashedPassword = BcryptUtil.bcryptHash(generateStrongPassword(random.nextInt(8, 12)));

        // Create and persist the new user
        User user = new User();
        user.name = oauthLoginDTO.getName();
        user.email = oauthLoginDTO.getEmail();
        user.password = hashedPassword;
        user.verifiedAt = now;
        user.persist();

        return TokenResponse.build(user, uriInfo);
    }
}
