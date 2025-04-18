package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.ProductCollectionConditionDTO;
import org.aicart.store.product.dto.ProductCollectionDTO;
import org.aicart.store.product.request.ValueNameRequest;

import java.util.Map;

@Path("/product/collections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductCollectionResource {

    @Inject
    ProductCollectionService service;

    @GET
    @Path("/")
    public Response getAll() {
        return Response.ok(service.getAll()).build();
    }

    @POST
    @Path("/")
    public Response create(@Valid ProductCollectionDTO request) {

        for(ProductCollectionConditionDTO conditionDTO: request.conditions) {
            System.out.println(conditionDTO.field);
            System.out.println(conditionDTO.operator);
            System.out.println(conditionDTO.value);
        }


        if(request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Request body is required"))
                    .build();
        }
        return Response.ok(service.create(request.title)).build();
    }
}
