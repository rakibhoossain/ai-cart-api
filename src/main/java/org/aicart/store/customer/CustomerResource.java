package org.aicart.store.customer;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.dto.CustomerDTO;
import org.aicart.store.customer.mapper.CustomerAddressMapper;
import org.aicart.store.customer.mapper.CustomerMapper;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    public Response createCustomer(@Valid CustomerDTO dto) {
        return Response.status(Response.Status.CREATED)
                .entity(CustomerMapper.toDto(customerService.createCustomer(dto)))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        return customerService.getCustomer(id)
                .map(customer -> Response.ok(CustomerMapper.toDto(customer)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, @Valid CustomerDTO dto) {
        return Response.ok(CustomerMapper.toDto(customerService.updateCustomer(id, dto))).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{customerId}/primary-address/{addressId}")
    public Response setPrimaryAddress(
            @PathParam("customerId") Long customerId,
            @PathParam("addressId") Long addressId
    ) {
        return Response.ok(CustomerMapper.toDto(customerService.setPrimaryAddress(customerId, addressId))).build();
    }

    @GET
    @Path("/{customerId}/primary-address")
    public Response getPrimaryAddress(@PathParam("customerId") Long customerId) {
        return customerService.getCustomer(customerId)
                .map(customer -> Response.ok(CustomerAddressMapper.toDto(customer.primaryAddress)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
