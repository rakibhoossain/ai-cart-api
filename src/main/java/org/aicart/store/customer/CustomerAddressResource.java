package org.aicart.store.customer;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.mapper.CustomerAddressMapper;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/customers/{customerId}/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer Addresses", description = "Customer address management operations")
public class CustomerAddressResource {

    @Inject
    CustomerAddressService addressService;

    // Legacy endpoints for backward compatibility
    @GET
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Get all addresses for a customer", description = "Retrieve all addresses associated with a specific customer")
    @APIResponse(responseCode = "200", description = "List of customer addresses")
    @APIResponse(responseCode = "404", description = "Customer not found")
    public Response listAddresses(@PathParam("customerId") Long customerId) {
        try {
            List<CustomerAddressDTO> addresses = addressService.getCustomerAddresses(customerId)
                    .stream().map(CustomerAddressMapper::toDto).toList();
            return Response.ok(addresses).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to retrieve addresses: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Create a new address", description = "Create a new address for a customer")
    @APIResponse(responseCode = "201", description = "Address created successfully")
    @APIResponse(responseCode = "400", description = "Invalid address data")
    public Response createAddress(
            @PathParam("customerId") Long customerId,
            @Valid CustomerAddressDTO dto
    ) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(CustomerAddressMapper.toDto(addressService.createAddress(customerId, dto)))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to create address: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{addressId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Update an address", description = "Update an existing customer address")
    @APIResponse(responseCode = "200", description = "Address updated successfully")
    @APIResponse(responseCode = "404", description = "Address not found")
    public Response updateAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId,
            @Valid CustomerAddressDTO dto
    ) {
        try {
            return Response.ok(CustomerAddressMapper.toDto(addressService.updateAddress(customerId, addressId, dto))).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to update address: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{addressId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Delete an address", description = "Delete a customer address")
    @APIResponse(responseCode = "204", description = "Address deleted successfully")
    @APIResponse(responseCode = "404", description = "Address not found")
    @APIResponse(responseCode = "400", description = "Cannot delete the only address")
    public Response deleteAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId
    ) {
        try {
            addressService.deleteAddress(customerId, addressId);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Failed to delete address: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{addressId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Get a specific address", description = "Retrieve a specific address by ID")
    @APIResponse(responseCode = "200", description = "Customer address details")
    @APIResponse(responseCode = "404", description = "Address not found")
    public Response getAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId
    ) {
        try {
            var address = addressService.getAddressForCustomer(addressId, customerId);
            return Response.ok(CustomerAddressMapper.toDto(address)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Address not found: " + e.getMessage()))
                    .build();
        }
    }

    // Helper classes for responses
    public static class ErrorResponse {
        public String error;
        public ErrorResponse(String error) { this.error = error; }
    }
}
