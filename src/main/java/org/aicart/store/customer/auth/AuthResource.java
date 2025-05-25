package org.aicart.store.customer.auth;

import io.quarkus.security.Authenticated;
import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.ChangePasswordDTO;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.authentication.dto.OauthLoginDTO;
import org.aicart.authentication.dto.ResetPasswordDTO;
import org.aicart.store.customer.auth.dto.CustomerRegistrationDTO;
import org.aicart.store.customer.auth.service.CustomerEmailVerification;
import org.aicart.store.customer.auth.service.CustomerLogin;
import org.aicart.store.customer.auth.service.CustomerPasswordReset;
import org.aicart.store.customer.auth.service.CustomerRegistration;
import org.aicart.store.user.auth.dto.RegistrationDTO;
import org.jboss.resteasy.reactive.NoCache;

import java.util.Map;

@Path("/customers/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    HttpHeaders httpHeaders;

    @Inject
    CustomerLogin customerLogin;

    @Inject
    CustomerEmailVerification customerEmailVerification;

    @Inject
    CustomerPasswordReset customerPasswordReset;

    @Inject
    CustomerRegistration customerRegistration;

    @POST
    @Path("/login")
    public Response login(@Valid LoginCredentialDTO loginCredentialDTO) {
        return customerLogin.login(loginCredentialDTO);
    }

    @POST
    @Path("/oauth-login")
    public Response oauthLogin(@Valid OauthLoginDTO oauthLoginDTO) {
        return customerLogin.oauthLogin(oauthLoginDTO);
    }

    @POST
    @Path("/register")
    @NoCache
    public Response register(@Valid CustomerRegistrationDTO registrationDTO, @HeaderParam("Origin") String origin) {
        return customerRegistration.register(registrationDTO, origin);
    }

    @GET
    @Path("/email-verify-code")
    @Authenticated
    public Response emailVerifyCode(@QueryParam("code") String code) {
        return customerEmailVerification.emailVerifyCode(code);
    }


    @GET
    @Path("/email-verify-token")
    public Response emailVerifyToken(@QueryParam("token") String token) {
        return customerEmailVerification.emailVerifyToken(token);
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

        return customerLogin.changePassword(changePasswordDTO);
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

        return customerPasswordReset.forgetPassword(email, origin);

    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(@Valid ResetPasswordDTO resetPasswordDTO) {
        return customerPasswordReset.resetPassword(resetPasswordDTO);
    }

}
