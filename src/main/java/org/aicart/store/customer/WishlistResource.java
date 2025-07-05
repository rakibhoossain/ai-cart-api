package org.aicart.store.customer;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.aicart.store.customer.dto.WishlistRequestDTO;
import org.aicart.store.customer.dto.WishlistResponseDTO;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.service.WishlistService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/customers/wishlist")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class WishlistResource {

    @Inject
    JsonWebToken jwt;

    @Context
    SecurityContext securityContext;

    @Inject
    WishlistService wishlistService;

    private Customer getCurrentCustomer() {
        if (jwt.getSubject() == null) {
            return null;
        }
        return Customer.findById(Long.valueOf(jwt.getSubject()));
    }

    @POST
    public Response addToWishlist(@Valid WishlistRequestDTO request) {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        boolean added = wishlistService.addToWishlist(customer, request.productId);
        if (!added) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Product already in wishlist or not found"))
                    .build();
        }

        return Response.ok(Map.of("message", "Product added to wishlist")).build();
    }

    @DELETE
    @Path("/{productId}")
    public Response removeFromWishlist(@PathParam("productId") Long productId) {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        boolean removed = wishlistService.removeFromWishlist(customer, productId);
        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Product not found in wishlist"))
                    .build();
        }

        return Response.ok(Map.of("message", "Product removed from wishlist")).build();
    }

    @GET
    public Response getWishlist(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        WishlistResponseDTO response = wishlistService.getWishlist(customer, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("/check/{productId}")
    public Response checkWishlist(@PathParam("productId") Long productId) {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        boolean inWishlist = wishlistService.isInWishlist(customer, productId);
        return Response.ok(Map.of("inWishlist", inWishlist)).build();
    }

    @GET
    @Path("/count")
    public Response getWishlistCount() {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        long count = wishlistService.getWishlistCount(customer);
        return Response.ok(Map.of("count", count)).build();
    }

    @GET
    @Path("/product-ids")
    public Response getWishlistProductIds() {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("message", "Authentication required"))
                    .build();
        }

        List<Long> productIds = wishlistService.getWishlistProductIds(customer);
        return Response.ok(Map.of("productIds", productIds)).build();
    }
}
