package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.entity.Category;
import jakarta.validation.Valid;
import org.aicart.store.product.request.CategoryCreateRequest;
import org.aicart.store.product.request.CategoryUpdateRequest;
import org.aicart.store.product.dto.CategoryDTO;
import org.aicart.store.product.mapper.CategoryMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryService categoryService;
    
    @Inject
    CategoryMapper categoryMapper;

    @GET
    public Response getCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("order") @DefaultValue("asc") String sortOrder
    ) {
        List<Category> categories = categoryService.getCategories(page, size, sortField, 
                "asc".equalsIgnoreCase(sortOrder));
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
    
    @GET
    @Path("/tree")
    public Response getCategoryTree() {
        List<Object[]> categoryTree = categoryService.getCategoryTree();
        return Response.ok(categoryMapper.toDtoTree(categoryTree)).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCategory(@PathParam("id") Long id) {
        Category category = Category.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Category not found"))
                    .build();
        }
        return Response.ok(categoryMapper.toDto(category)).build();
    }

    @POST
    public Response addCategory(@Valid CategoryCreateRequest request) {
        Category category = categoryService.addCategory(request.getName(), request.getParentId());
        return Response.status(Response.Status.CREATED)
                .entity(categoryMapper.toDto(category))
                .build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateCategory(
            @PathParam("id") Long id, 
            @Valid CategoryUpdateRequest request
    ) {
        Category category = Category.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Category not found"))
                    .build();
        }
        
        category.name = request.getName();
        category.persist();
        categoryService.invalidateCache();
        
        return Response.ok(categoryMapper.toDto(category)).build();
    }

    @PUT
    @Path("/{id}/parent/{parentId}")
    public Response updateParent(@PathParam("id") Long id, @PathParam("parentId") Long newParentId) {
        try {
            categoryService.updateParent(id, newParentId);
            Category category = Category.findById(id);
            return Response.ok(categoryMapper.toDto(category)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}/move-subtree/{newParentId}")
    public Response moveSubtree(@PathParam("id") Long id, @PathParam("newParentId") Long newParentId) {
        try {
            categoryService.moveSubtree(id, newParentId);
            return Response.ok(Map.of("message", "Subtree moved successfully")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        try {
            categoryService.deleteCategory(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/descendants")
    public Response getDescendants(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        List<Category> categories = categoryService.getDescendants(id);
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}/ancestors")
    public Response getAncestors(@PathParam("id") Long id) {
        List<Category> categories = categoryService.getAncestors(id);
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
    
    @GET
    @Path("/{id}/with-depth")
    public Response getCategoriesWithDepth(
            @PathParam("id") Long id,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        List<Object[]> categoriesWithDepth = categoryService.getCategoriesWithDepth(id, page, size);
        return Response.ok(categoryMapper.toDtoWithDepth(categoriesWithDepth)).build();
    }
    
    @GET
    @Path("/count-descendants/{id}")
    public Response countDescendants(@PathParam("id") Long id) {
        long count = categoryService.countDescendants(id);
        return Response.ok(Map.of("count", count)).build();
    }
}