package org.aicart.store.review;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.review.dto.ProductReviewCreateDTO;
import org.aicart.store.review.dto.ProductReviewDTO;
import org.aicart.store.review.service.ProductReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Path("/product/{productId}/reviews")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductReviewResource {

    @Inject
    ProductReviewService service;

    @GET
    public Response list(
            @PathParam("productId") Long productId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("newest") String sort
    ) {
        List<ProductReviewDTO> dtos = service.getReviews(productId, page, size, sort)
                .stream()
                .map(ProductReviewDTO::fromEntity)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @POST
    public Response create(
            @PathParam("productId") Long productId,
            @Valid ProductReviewCreateDTO request
    ) {
        // TODO: inject logged-in customer from security context if available
        Customer customer = null; // Replace with real auth fetch
        var review = service.createReview(productId, request, customer);
        return Response.ok(ProductReviewDTO.fromEntity(review)).status(Response.Status.CREATED).build();
    }
}
