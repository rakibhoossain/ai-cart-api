package store.aicart.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import store.aicart.product.entity.Category;

import java.util.List;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @POST
    public Response addCategory(@QueryParam("name") String name, @QueryParam("parentId") Long parentId) {
        Category category = categoryService.addCategory(name, parentId);
        return Response.ok(category.name).build();
    }

    @PUT
    @Path("/{id}/parent")
    public Response updateParent(@PathParam("id") Long id, @QueryParam("newParentId") Long newParentId) {
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
