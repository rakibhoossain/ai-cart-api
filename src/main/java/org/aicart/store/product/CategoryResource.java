package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.entity.Category;
import jakarta.validation.Valid;
import org.aicart.store.product.request.CategoryCreateRequest;

import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @GET
    public Response getCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        List<Category> categories = categoryService.getCategories(page, size);
        return Response.ok(categories).build();
    }

    @POST
    public Response addCategory(@Valid CategoryCreateRequest request) {
        Category category = categoryService.addCategory(request.getName(), request.getParentId());
        return Response.ok(category.name).build();
    }

    @PUT
    @Path("/{id}/parent/{parentId}")
    public Response updateParent(@PathParam("id") Long id, @PathParam("parentId") Long newParentId) {
        categoryService.updateParent(id, newParentId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        categoryService.deleteCategory(id);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}/descendants")
    public Response getDescendants(@PathParam("id") Long id) {
        List<Category> categories = categoryService.getDescendants(id);
        return Response.ok(categories).build();
    }

    @GET
    @Path("/{id}/ancestors")
    public Response getAncestors(@PathParam("id") Long id) {
        List<Category> categories = categoryService.getAncestors(id);
        return Response.ok(categories).build();
    }
}
