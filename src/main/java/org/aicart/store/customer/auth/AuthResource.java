package org.aicart.store.customer.auth;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.authentication.dto.LoginCredentialDTO;
import org.aicart.store.customer.auth.service.CustomerLogin;

@Path("/customers/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    HttpHeaders httpHeaders;

    @Inject
    CustomerLogin customerLogin;

    @POST
    @Path("/login")
    public Response login(@Valid LoginCredentialDTO loginCredentialDTO) {
        return customerLogin.login(loginCredentialDTO);
    }

}
