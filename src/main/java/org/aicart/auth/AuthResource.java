package org.aicart.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import java.util.*;
import jakarta.ws.rs.core.*;
import org.aicart.auth.dto.ChangePasswordDTO;
import org.aicart.auth.dto.ResetPasswordDTO;
import org.aicart.auth.service.ResetPassword;
import org.aicart.auth.service.UserLogin;
import org.aicart.auth.service.UserRegistration;
import org.aicart.auth.dto.LoginCredentialDTO;
import org.jboss.resteasy.reactive.NoCache;
import org.aicart.auth.dto.RegistrationDTO;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UserLogin userLogin;

    @Inject
    EmailVerifyService emailVerifyService;

    @Inject
    ResetPassword resetPassword;

    @Inject
    ChangePasswordService changePasswordService;

    @Inject
    UserRegistration userRegistration;

    @POST
    @Path("/login")
    public Response login(@Valid LoginCredentialDTO loginCredentialDTO) {
        return userLogin.login(loginCredentialDTO);
    }


    @POST
    @Path("/register")
    @NoCache
    public Response register(@Valid RegistrationDTO registrationDTO) {
        return userRegistration.register(registrationDTO);
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
                    .entity(Map.of("error", "Request body is required"))
                    .build();
        }

        return changePasswordService.changePassword(changePasswordDTO.getCurrentPassword(), changePasswordDTO.getPassword());
    }


    @GET
    @Path("/reset-password")
    public Response resetPassword(@QueryParam("email") String email) {
        return resetPassword.resetPassword(email);

    }

    @POST
    @Path("/update-password")
    public Response updatePassword(@Valid ResetPasswordDTO resetPasswordDTO) {
        return resetPassword.updatePassword(resetPasswordDTO);
    }
}

