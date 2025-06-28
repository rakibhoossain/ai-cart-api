package org.aicart.blog.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.blog.dto.BlogTagDTO;
import org.aicart.blog.service.BlogTagService;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/blog-tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogTagResource {
    
    @Inject
    BlogTagService tagService;
    
    @Inject
    JsonWebToken jwt;
    
    @GET
    @Authenticated
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("direction") @DefaultValue("asc") String sortDirection,
            @QueryParam("search") String searchQuery) {

        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        List<BlogTagDTO> tags = tagService.findByShop(
                shop, 
                page, 
                size, 
                sortField, 
                "asc".equalsIgnoreCase(sortDirection),
                searchQuery);
        
        long total = tagService.countByShop(shop, searchQuery);
        
        return Response.ok(Map.of(
                "data", tags,
                "total", total,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/{id}")
    @Authenticated
    public Response get(@PathParam("id") Long id) {
        BlogTagDTO tag = tagService.findById(id);
        return Response.ok(tag).build();
    }
    
    @POST
    @Authenticated
    public Response create(@Valid BlogTagDTO dto) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogTagDTO created = tagService.create(dto, shop);
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
    public Response update(@PathParam("id") Long id, @Valid BlogTagDTO dto) {
        User user = User.findById(Long.parseLong(jwt.getSubject()));
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        try {
            BlogTagDTO updated = tagService.update(id, dto, shop);
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
            tagService.delete(id, shop);
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
    public Response listPublic(
            @QueryParam("shopId") Long shopId,
            @QueryParam("withCounts") @DefaultValue("false") boolean withCounts) {
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }

        List<BlogTagDTO> tags;
        if (withCounts) {
            tags = tagService.findAllByShopWithCounts(shop);
        } else {
            tags = tagService.findAllByShop(shop);
        }

        return Response.ok(Map.of("data", tags)).build();
    }
}