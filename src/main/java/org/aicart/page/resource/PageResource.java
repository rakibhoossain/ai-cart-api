package org.aicart.page.resource;

import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.entity.Language;
import org.aicart.page.dto.PageDTO;
import org.aicart.page.service.PageService;
import org.aicart.store.context.ShopContext;
import org.aicart.store.user.entity.Shop;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/pages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PageResource {
    
    @Inject
    PageService pageService;
    
    @Inject
    JsonWebToken jwt;

    @Inject
    ShopContext shopContext;
    
    @GET
    @Authenticated
    public Response list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("direction") @DefaultValue("asc") String sortDirection,
            @QueryParam("search") String searchQuery) {

        Shop shop = Shop.findById(shopContext.getShopId());
        
        List<PageDTO> pages = pageService.findByShop(
                shop, 
                page, 
                size, 
                sortField, 
                "asc".equalsIgnoreCase(sortDirection),
                searchQuery);
        
        long total = pageService.countByShop(shop, searchQuery);
        
        return Response.ok(Map.of(
                "data", pages,
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
        
        PageDTO page = pageService.findById(id, language);
        return Response.ok(page).build();
    }
    
    @POST
    @Authenticated
    public Response create(@Valid PageDTO dto) {
        Shop shop = Shop.findById(shopContext.getShopId());
        
        try {
            PageDTO created = pageService.create(dto, shop);
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
    public Response update(@PathParam("id") Long id, @Valid PageDTO dto) {
        Shop shop = Shop.findById(shopContext.getShopId());
        
        try {
            PageDTO updated = pageService.update(id, dto, shop);
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
        Shop shop = Shop.findById(shopContext.getShopId());
        
        try {
            pageService.delete(id, shop);
            return Response.noContent().build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/status")
    @Authenticated
    public Response updateStatus(
            @PathParam("id") Long id,
            @QueryParam("active") @DefaultValue("true") boolean active) {

        Shop shop = Shop.findById(shopContext.getShopId());
        
        try {
            PageDTO updated = pageService.updateStatus(id, active, shop);
            return Response.ok(updated).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @GET
    @Path("/public/{slug}")
    @Blocking
    public Response getPublicBySlug(
            @PathParam("slug") String slug,
            @QueryParam("languageId") @DefaultValue("1") Long languageId) {
        
        Shop shop = Shop.findById(shopContext.getShopId());
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
            PageDTO page = pageService.findBySlug(slug, shop, language);
            return Response.ok(page).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Page not found"))
                    .build();
        }
    }
}