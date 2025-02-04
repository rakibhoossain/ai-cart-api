package org.aicart.store.product;


import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.AttributeRequestDTO;
import org.aicart.store.product.dto.AttributeResponseDTO;
import org.aicart.store.product.dto.AttributeValueRequestDTO;
import org.aicart.store.product.dto.AttributeValueResponseDTO;
import org.aicart.store.product.entity.Attribute;
import org.aicart.store.product.entity.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/product-attributes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductAttributeResource {

    @Inject
    ProductAttributeService productAttributeService;

    @GET
    public Response getAttributes(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {

        List<AttributeResponseDTO> attributes = productAttributeService.getAttributes(page, size)
                .stream().map(AttributeResponseDTO::new).collect(Collectors.toList());

        return Response.ok(attributes).build();
    }

    @POST
    public Response createAttribute(@Valid AttributeRequestDTO attributeRequestDTO)
    {
        try {
            Attribute attribute = productAttributeService.createAttribute(attributeRequestDTO);

            return Response.ok(new AttributeResponseDTO(attribute)).build();
        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }


    @GET
    @Path("values/{attributeId}")
    public Response getAttributeValues(@PathParam("attributeId") long attributeId) {

        List<AttributeValueResponseDTO> attributeValues = productAttributeService.getAttributeValues(attributeId)
                .stream().map(AttributeValueResponseDTO::new).collect(Collectors.toList());

        return Response.ok(attributeValues).build();
    }

    @POST
    @Path("values/{attributeId}")
    public Response createAttributeValue(@PathParam("attributeId") long attributeId, @Valid AttributeValueRequestDTO attributeValueRequestDTO) {

        try {

            AttributeValue attributeValue = productAttributeService.createAttributeValue(attributeId, attributeValueRequestDTO);
            return Response.ok(new AttributeValueResponseDTO(attributeValue)).build();

        } catch (ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
}
