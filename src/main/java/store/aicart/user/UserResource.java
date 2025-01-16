package store.aicart.user;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;
import store.aicart.order.dto.OrderBillingDTO;
import store.aicart.order.dto.OrderShippingDTO;
import store.aicart.user.entity.User;

import java.util.Map;

@Path("/users")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    SecurityIdentity identity;

    @Inject
    JsonWebToken jwt;

    @Inject
    UserService userService;

    @GET
    @Path("/me")
    @NoCache
    public String me() {
        return identity.getPrincipal().toString();
    }

    @POST
    @Path("/update-billing")
    @Transactional
    public Response updateBilling(@Valid OrderBillingDTO orderBillingDTO) {

        if (orderBillingDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        String subject = jwt.getSubject();

        // Find the user by id
        User user = User.find("id", subject).firstResult();

        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Invalid action"))
                    .build();
        }

        userService.storeUserBilling(user, orderBillingDTO);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Billing address saved successfully"))
                .build();

    }


    @POST
    @Path("/update-shipping")
    @Transactional
    public Response updateShipping(@Valid OrderShippingDTO orderShippingDTO) {

        if (orderShippingDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }

        String subject = jwt.getSubject();

        // Find the user by id
        User user = User.find("id", subject).firstResult();

        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Invalid action"))
                    .build();
        }

        userService.storeUserShipping(user, orderShippingDTO);

        return Response.status(Response.Status.OK)
                .entity(Map.of("message", "Shipping address saved successfully"))
                .build();
    }
}
