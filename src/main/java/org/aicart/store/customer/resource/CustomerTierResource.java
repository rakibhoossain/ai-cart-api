package org.aicart.store.customer.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.customer.entity.CustomerTier;
import org.aicart.store.customer.CustomerRepository;
import org.aicart.store.customer.service.CustomerTierService;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/api/customers/{customerId}/tier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer Tier Management", description = "Customer tier management operations")
public class CustomerTierResource {

    @Inject
    CustomerTierService customerTierService;

    @Inject
    CustomerRepository customerRepository;



    @GET
    @Operation(summary = "Get customer tier info", description = "Get current tier and requirements")
    @APIResponse(responseCode = "200", description = "Tier info retrieved successfully")
    public Response getTierInfo(
            @HeaderParam("Shop-Id") Long shopId,
            @PathParam("customerId") Long customerId) {

        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Customer not found"))
                    .build();
        }

        CustomerTierService.TierRequirements requirements = customerTierService.getTierRequirements();

        return Response.ok(Map.of(
                "current_tier", customer.customerTier,
                "tier_overridden", customer.tierOverridden != null ? customer.tierOverridden : false,
                "tier_override_reason", customer.tierOverrideReason,
                "tier_updated_at", customer.tierUpdatedAt,
                "total_spent", customer.totalSpent,
                "total_orders", customer.totalOrders,
                "requirements", Map.of(
                        "bronze", requirements.bronze,
                        "silver", requirements.silver,
                        "gold", requirements.gold,
                        "platinum", requirements.platinum,
                        "diamond", requirements.diamond
                )
        )).build();
    }

    @POST
    @Path("/recalculate")
    @Operation(summary = "Recalculate customer tier", description = "Recalculate tier based on current spending")
    @APIResponse(responseCode = "200", description = "Tier recalculated successfully")
    public Response recalculateTier(
            @HeaderParam("Shop-Id") Long shopId,
            @PathParam("customerId") Long customerId) {

        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Customer not found"))
                    .build();
        }

        CustomerTier newTier = customerTierService.calculateTier(customer);

        return Response.ok(Map.of(
                "previous_tier", customer.customerTier,
                "new_tier", newTier,
                "total_spent", customer.totalSpent
        )).build();
    }

    @POST
    @Path("/override")
    @Operation(summary = "Override customer tier", description = "Manually set customer tier")
    @APIResponse(responseCode = "200", description = "Tier overridden successfully")
    public Response overrideTier(
            @HeaderParam("Shop-Id") Long shopId,
            @PathParam("customerId") Long customerId,
            @Valid TierOverrideRequest request) {

        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Customer not found"))
                    .build();
        }

        try {
            CustomerTier newTier = CustomerTier.valueOf(request.tier.toUpperCase());
            customerTierService.overrideTier(customer, newTier, request.updatedBy, request.reason);

            return Response.ok(Map.of(
                    "message", "Tier overridden successfully",
                    "new_tier", newTier,
                    "reason", request.reason
            )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Invalid tier: " + request.tier))
                    .build();
        }
    }

    @DELETE
    @Path("/override")
    @Operation(summary = "Reset tier override", description = "Remove manual tier override and recalculate")
    @APIResponse(responseCode = "200", description = "Tier override reset successfully")
    public Response resetTierOverride(
            @HeaderParam("Shop-Id") Long shopId,
            @PathParam("customerId") Long customerId,
            @QueryParam("updated_by") String updatedBy) {

        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Customer not found"))
                    .build();
        }

        CustomerTier calculatedTier = customerTierService.resetTierOverride(customer, updatedBy != null ? updatedBy : "system");

        return Response.ok(Map.of(
                "message", "Tier override reset successfully",
                "calculated_tier", calculatedTier,
                "total_spent", customer.totalSpent
        )).build();
    }

    @POST
    @Path("/bulk-update")
    @Operation(summary = "Bulk update tiers", description = "Update tiers for all customers in shop")
    @APIResponse(responseCode = "200", description = "Bulk update completed")
    public Response bulkUpdateTiers(
            @HeaderParam("Shop-Id") Long shopId) {

        // TODO: Implement bulk update when shop service is available
        return Response.ok(Map.of(
                "message", "Bulk tier update not yet implemented",
                "updated_count", 0
        )).build();
    }

    // DTO for tier override request
    public static class TierOverrideRequest {
        public String tier;
        public String reason;
        public String updatedBy;
    }
}
