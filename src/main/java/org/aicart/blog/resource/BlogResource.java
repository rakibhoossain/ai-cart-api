package org.aicart.blog.resource;

import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.aicart.blog.dto.BlogDTO;
import org.aicart.blog.entity.BlogStatus;
import org.aicart.blog.service.BlogService;
import org.aicart.entity.Language;
import org.aicart.store.user.entity.Shop;
import org.aicart.store.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/blogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogResource {
    
    @Inject
    BlogService blogService;
    
    @GET
    @Authenticated
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("createdAt") String sortField,
            @QueryParam("direction") @DefaultValue("desc") String sortDirection,
            @QueryParam("search") String searchQuery,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {

        Shop shop = Shop.findById(1); // TODO: get shop from user
        Language language = Language.findById(languageId);
        
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        List<BlogDTO> blogs = blogService.findByShop(
                shop, 
                page, 
                size, 
                sortField, 
                "asc".equalsIgnoreCase(sortDirection),
                searchQuery,
                language);
        
        long total = blogService.countByShop(shop, searchQuery, language);
        
        return Response.ok(Map.of(
                "data", blogs,
                "total", total,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/{id}")
    @Authenticated
    public Response get(@PathParam("id") Long id, 
                        @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        Language language = Language.findById(languageId);
        
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        BlogDTO blog = blogService.findById(id, language);
        return Response.ok(blog).build();
    }
    
    @POST
    @Authenticated
    public Response create(@Valid BlogDTO dto) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        BlogDTO created = blogService.create(dto, shop);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    @Authenticated
    public Response update(@PathParam("id") Long id, @Valid BlogDTO dto) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        BlogDTO updated = blogService.update(id, dto, shop);
        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("/{id}")
    @Authenticated
    public Response delete(@PathParam("id") Long id) {
        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        blogService.delete(id, shop);
        return Response.noContent().build();
    }
    
    @PUT
    @Path("/{id}/status")
    @Authenticated
    public Response updateStatus(
            @PathParam("id") Long id,
            @QueryParam("status") @DefaultValue("DRAFT") BlogStatus status) {

        Shop shop = Shop.findById(1); // TODO: get shop from user
        
        BlogDTO updated = blogService.updateStatus(id, status, shop);
        return Response.ok(updated).build();
    }
    
    @GET
    @Path("/public")
    @Blocking
    public Response listPublic(
            @QueryParam("shopId") Long shopId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Language language = Language.findById(languageId);
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        List<BlogDTO> blogs = blogService.findPublishedByShop(shop, page, size, language);
        
        return Response.ok(Map.of(
                "data", blogs,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/public/{slug}")
    @Blocking
    public Response getPublicBySlug(
            @PathParam("slug") String slug,
            @QueryParam("shopId") Long shopId,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Language language = Language.findById(languageId);
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        try {
            BlogDTO blog = blogService.findBySlug(slug, shop, language);
            
            // Increment view count asynchronously
            new Thread(() -> blogService.incrementViewCount(blog.getId())).start();
            
            return Response.ok(blog).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Blog not found"))
                    .build();
        }
    }
    
    @GET
    @Path("/public/category/{categoryId}")
    @Blocking
    public Response getByCategory(
            @PathParam("categoryId") Long categoryId,
            @QueryParam("shopId") Long shopId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Language language = Language.findById(languageId);
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        List<BlogDTO> blogs = blogService.findByCategory(categoryId, shop, page, size, language);
        
        return Response.ok(Map.of(
                "data", blogs,
                "page", page,
                "size", size
        )).build();
    }
    
    @GET
    @Path("/public/tag/{tagId}")
    @Blocking
    public Response getByTag(
            @PathParam("tagId") Long tagId,
            @QueryParam("shopId") Long shopId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        
        Shop shop = Shop.findById(shopId);
        if (shop == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Language language = Language.findById(languageId);
        if (language == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", "Language not found"))
                    .build();
        }
        
        List<BlogDTO> blogs = blogService.findByTag(tagId, shop, page, size, language);
        
        return Response.ok(Map.of(
                "data", blogs,
                "page", page,
                "size", size
        )).build();
    }
}