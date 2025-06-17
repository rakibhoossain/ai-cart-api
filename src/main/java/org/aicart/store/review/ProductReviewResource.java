package org.aicart.store.review;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import org.aicart.store.context.ShopContext;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.review.dto.ProductReviewCreateDTO;
import org.aicart.store.review.dto.ProductReviewDTO;
import org.aicart.store.review.service.ProductReviewService;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/product/{productId}/reviews")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductReviewResource {

    @Inject
    ShopContext shopContext;

    @Inject
    ProductReviewService service;
    
    @Inject
    JsonWebToken jwt;
    
    @Context
    SecurityContext securityContext;

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
        // Get customer from token if available, otherwise null
        Customer customer = null;
        if (securityContext.getUserPrincipal() != null && jwt.getSubject() != null) {
            try {
                customer = Customer.findById(Long.valueOf(jwt.getSubject()));
            } catch (Exception e) {
                // Token exists but customer not found - continue as guest
            }
        }
        
        Shop shop = Shop.findById(shopContext.getShopId());
        if(shop == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if(customer != null && !customer.shop.equals(shop)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        var review = service.createReview(shop, productId, request, customer);
        return Response.ok(ProductReviewDTO.fromEntity(review)).status(Response.Status.CREATED).build();
    }
}