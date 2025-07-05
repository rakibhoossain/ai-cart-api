package org.aicart.store.customer.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.customer.dto.CustomerTagDTO;
import org.aicart.store.customer.service.CustomerTagService;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/customer-tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer Tags", description = "Customer tag management operations")
public class CustomerTagResource {

    @Inject
    CustomerTagService customerTagService;

    @GET
    @Operation(summary = "Get customer tags", description = "Get paginated list of customer tags with optional search")
    @APIResponse(responseCode = "200", description = "List of customer tags")
    public Response getTags(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("order") @DefaultValue("asc") String order,
            @QueryParam("search") String searchQuery,
            @QueryParam("with_counts") @DefaultValue("false") boolean withCounts) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            boolean ascending = "asc".equalsIgnoreCase(order);

            List<CustomerTagDTO> tags;
            if (withCounts) {
                tags = customerTagService.findAllByShopWithCounts(shop);
            } else {
                tags = customerTagService.findByShop(shop, page, size, sortField, ascending, searchQuery);
            }

            long total = customerTagService.countByShop(shop, searchQuery);

            return Response.ok(Map.of(
                    "data", tags,
                    "total", total,
                    "page", page,
                    "size", size
            )).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch customer tags: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get customer tag by ID", description = "Get a specific customer tag by ID")
    @APIResponse(responseCode = "200", description = "Customer tag details")
    @APIResponse(responseCode = "404", description = "Customer tag not found")
    public Response getTag(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("id") Long id) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            Optional<CustomerTagDTO> tag = customerTagService.findById(id, shop);
            if (tag.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Customer tag not found"))
                        .build();
            }

            return Response.ok(tag.get()).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch customer tag: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Operation(summary = "Create customer tag", description = "Create a new customer tag")
    @APIResponse(responseCode = "201", description = "Customer tag created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input or tag name already exists")
    public Response createTag(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @Valid CustomerTagDTO dto) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            CustomerTagDTO created = customerTagService.create(dto, shop);
            return Response.status(Response.Status.CREATED).entity(created).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to create customer tag: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update customer tag", description = "Update an existing customer tag")
    @APIResponse(responseCode = "200", description = "Customer tag updated successfully")
    @APIResponse(responseCode = "404", description = "Customer tag not found")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public Response updateTag(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("id") Long id,
            @Valid CustomerTagDTO dto) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            CustomerTagDTO updated = customerTagService.update(id, dto, shop);
            return Response.ok(updated).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to update customer tag: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete customer tag", description = "Delete a customer tag")
    @APIResponse(responseCode = "204", description = "Customer tag deleted successfully")
    @APIResponse(responseCode = "404", description = "Customer tag not found")
    public Response deleteTag(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("id") Long id) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            customerTagService.delete(id, shop);
            return Response.noContent().build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to delete customer tag: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/all")
    @Operation(summary = "Get all customer tags", description = "Get all customer tags for a shop (no pagination)")
    @APIResponse(responseCode = "200", description = "List of all customer tags")
    public Response getAllTags(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            List<CustomerTagDTO> tags = customerTagService.findAllByShop(shop);
            return Response.ok(Map.of("data", tags)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch customer tags: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/customers/count")
    @Operation(summary = "Get customer count for tag", description = "Get the number of customers with this tag")
    @APIResponse(responseCode = "200", description = "Customer count")
    public Response getCustomerCount(
            @HeaderParam("Shop-Id") @DefaultValue("1") Long shopId,
            @PathParam("id") Long id) {

        try {
            Shop shop = Shop.findById(shopId);
            if (shop == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Shop not found"))
                        .build();
            }

            long count = customerTagService.countCustomersWithTag(id, shop);
            return Response.ok(Map.of("count", count)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get customer count: " + e.getMessage()))
                    .build();
        }
    }
}
