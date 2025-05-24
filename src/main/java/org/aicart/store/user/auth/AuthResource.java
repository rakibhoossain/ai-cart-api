package org.aicart.store.user.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import java.util.*;
import jakarta.ws.rs.core.*;
import org.aicart.auth.EmailVerifyService;
import org.aicart.authentication.PasswordResetService;
import org.aicart.authentication.dto.ChangePasswordDTO;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.authentication.dto.OauthLoginDTO;
import org.aicart.authentication.dto.ResetPasswordDTO;
import org.aicart.store.user.auth.dto.RegistrationDTO;
import org.aicart.store.user.auth.service.UserLogin;
import org.aicart.store.user.auth.service.UserRegistration;
import org.jboss.resteasy.reactive.NoCache;
import io.smallrye.faulttolerance.api.RateLimit;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    HttpHeaders httpHeaders;

    @Inject
    UserLogin userLogin;

    @Inject
    EmailVerifyService emailVerifyService;

    @Inject
    PasswordResetService resetPassword;

    @Inject
    UserRegistration userRegistration;

    @POST
    @Path("/login")
    public Response login(@Valid LoginCredentialDTO loginCredentialDTO) {
        return userLogin.login(loginCredentialDTO);
    }

    @POST
    @Path("/oauth-login")
    public Response oauthLogin(@Valid OauthLoginDTO oauthLoginDTO) {
        return userLogin.oauthLogin(oauthLoginDTO);
    }

    @POST
    @Path("/register")
    @NoCache
    public Response register(@Valid RegistrationDTO registrationDTO, @HeaderParam("Origin") String origin) {
        return userRegistration.register(registrationDTO, origin);
    }


    @GET
    @Path("/email-verify-code")
    public Response emailVerifyCode(@QueryParam("code") String code) {
        return emailVerifyService.emailVerifyCode(code);
    }


    @GET
    @Path("/email-verify-token")
    public Response emailVerifyToken(@QueryParam("token") String token) {
        return emailVerifyService.emailVerifyToken(token);
    }

    @POST
    @Path("/change-password")
    @NoCache
    public Response changePassword(@Valid ChangePasswordDTO changePasswordDTO) {
        if (changePasswordDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        return userLogin.changePassword(changePasswordDTO);
    }


    @GET
    @Path("/forget-password")
    @RateLimit(value = 3, window = 20) // 3 requests per 20 seconds
    public Response forgetPassword(@QueryParam("email") String email, @HeaderParam("Origin") String origin, @HeaderParam("Referer") String referer) {

        // Log all headers
        httpHeaders.getRequestHeaders().forEach((key, value) -> {
            System.out.println("Header: " + key + " -> Value: " + value);
        });

        System.out.println("Origin: " + origin + "\nReferer: " + referer);

        return resetPassword.forgetPassword(email, origin);

    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(@Valid ResetPasswordDTO resetPasswordDTO) {
        return resetPassword.resetPassword(resetPasswordDTO);
    }
}

