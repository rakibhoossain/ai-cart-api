package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.DiscountDTO;

import java.util.Map;

@Path("/product/discounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiscountResource {

    @Inject
    DiscountService discountService;

    @POST
    public Response createDiscount(@Valid DiscountDTO discountDTO) {
        try {
            DiscountDTO createdDiscount = discountService.createDiscount(discountDTO);
            return Response.status(Response.Status.CREATED).entity(createdDiscount).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
}
