package org.aicart.blog.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.blog.dto.BlogCategoryDTO;
import org.aicart.blog.service.BlogCategoryService;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/blog-categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogCategoryResource {
    
    @Inject
    BlogCategoryService categoryService;
    
    @GET
    @Authenticated
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("direction") @DefaultValue("asc") String sortDirection,
            @QueryParam("search") String searchQuery) {

        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        List<BlogCategoryDTO> categories = categoryService.findByShop(
                shop, 
                page, 
                size, 
                sortField, 
                "asc".equalsIgnoreCase(sortDirection),
                searchQuery);
        
        long total = categoryService.countByShop(shop, searchQuery);
        
        return Response.ok(Map.of(
                "data", categories,
                "total", total,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/{id}")
    @Authenticated
    public Response get(@PathParam("id") Long id) {
        BlogCategoryDTO category = categoryService.findById(id);
        return Response.ok(category).build();
    }
    
    @GET
    @Path("/root")
    @Authenticated
    public Response getRootCategories() {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        List<BlogCategoryDTO> categories = categoryService.findRootCategories(shop);
        return Response.ok(categories).build();
    }
    
    @GET
    @Path("/{parentId}/children")
    @Authenticated
    public Response getChildren(@PathParam("parentId") Long parentId) {
        List<BlogCategoryDTO> children = categoryService.findChildren(parentId);
        return Response.ok(children).build();
    }
    
    @POST
    @Authenticated
    public Response create(@Valid BlogCategoryDTO dto) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogCategoryDTO created = categoryService.create(dto, shop);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Authenticated
    public Response update(@PathParam("id") Long id, @Valid BlogCategoryDTO dto) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogCategoryDTO updated = categoryService.update(id, dto, shop);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Authenticated
    public Response delete(@PathParam("id") Long id) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            categoryService.delete(id, shop);
            return Response.noContent().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/public")
    public Response listPublic(@QueryParam("shopId") Long shopId) {
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        List<BlogCategoryDTO> categories = categoryService.findRootCategories(shop);
        return Response.ok(categories).build();
    }
}
