package org.aicart.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.ChangePasswordDTO;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.authentication.dto.OauthLoginDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public abstract class AuthenticationService implements AuthenticationInterface {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthenticationService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    abstract protected Response jwtResponse(LoginCredentialDTO loginCredentialDTO);

    protected boolean isInvalidCredentials(String password, IdentifiableEntity entity) {
        return entity == null || entity.getPassword() == null || entity.getPassword().isBlank() || !BcryptUtil.matches(password, entity.getPassword());
    }

    @Override
    public Response login(LoginCredentialDTO loginCredentialDTO) {
        if (loginCredentialDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        return jwtResponse(loginCredentialDTO);
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

            if(response.statusCode() == Response.Status.OK.getStatusCode()) {
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

            if(response.statusCode() == Response.Status.OK.getStatusCode()) {
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

    @Override
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
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Something went wrong"))
                    .build();
        }
    }

    protected abstract Response generateOauthToken(OauthLoginDTO oauthLoginDTO);

    protected abstract Response changePassword(ChangePasswordDTO changePasswordDTO);

    public String generatePasswordHash(String password) {
        return BcryptUtil.bcryptHash(password);
    }
}
