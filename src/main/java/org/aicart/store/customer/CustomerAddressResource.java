package org.aicart.store.customer;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.dto.CustomerAddressDTO;
import org.aicart.store.customer.mapper.CustomerAddressMapper;

import java.util.List;

@Path("/customers/{customerId}/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerAddressResource {

    @Inject
    CustomerAddressService addressService;

    @GET
    public Response listAddresses(@PathParam("customerId") Long customerId) {
        List<CustomerAddressDTO> addresses = addressService.getCustomerAddresses(customerId)
                .stream().map(CustomerAddressMapper::toDto).toList();
        return Response.ok(addresses).build();
    }

    @POST
    public Response createAddress(
            @PathParam("customerId") Long customerId,
            @Valid CustomerAddressDTO dto
    ) {
        return Response.status(Response.Status.CREATED)
                .entity(CustomerAddressMapper.toDto(addressService.createAddress(customerId, dto)))
                .build();
    }

    @PUT
    @Path("/{addressId}")
    public Response updateAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId,
            @Valid CustomerAddressDTO dto
    ) {
        return Response.ok(CustomerAddressMapper.toDto(addressService.updateAddress(customerId, addressId, dto))).build();
    }

    @DELETE
    @Path("/{addressId}")
    public Response deleteAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId
    ) {
        addressService.deleteAddress(customerId, addressId);
        return Response.noContent().build();
    }
}
