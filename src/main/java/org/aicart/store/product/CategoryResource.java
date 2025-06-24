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
import org.aicart.store.user.entity.Shop;

import java.util.ArrayList;
import java.util.HashMap;
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
    
    // For now, we'll hardcode the shop ID
    // Later this can be replaced with a dynamic lookup or injection
    private Long getShopId() {
        return 1L;
    }

    @GET
    public Response getCategories(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("name") String sortField,
            @QueryParam("order") @DefaultValue("asc") String sortOrder,
            @QueryParam("q") String searchQuery
    ) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        List<Category> categories = categoryService.getCategories(page, size, sortField, 
                "asc".equalsIgnoreCase(sortOrder), searchQuery, shop);
        long totalCount = categoryService.countCategories(sortField, "asc".equalsIgnoreCase(sortOrder), searchQuery, shop);
        
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = Map.of(
            "data", dtos,
            "total", totalCount,
            "page", page,
            "size", size
        );
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/tree")
    public Response getCategoryTree(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("q") String searchQuery
    ) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        // Get paginated root categories
        List<Category> rootCategories = categoryService.getRootCategories(page, size, searchQuery, shop);
        long totalCount = categoryService.countRootCategories(searchQuery, shop);
        
        // For each root category, load its complete subtree
        List<CategoryDTO> categoryTree = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            CategoryDTO rootDto = categoryMapper.toDto(rootCategory);
            
            // Load all descendants for this root category
            List<Category> descendants = categoryService.getDescendants(rootCategory.id, shop);
            
            // Build the tree structure
            Map<Long, CategoryDTO> dtoMap = new HashMap<>();
            dtoMap.put(rootCategory.id, rootDto);
            
            // First pass: create all DTOs
            for (Category descendant : descendants) {
                CategoryDTO dto = categoryMapper.toDto(descendant);
                dto.setChildren(new ArrayList<>());
                dtoMap.put(descendant.id, dto);
            }
            
            // Second pass: build the tree
            for (Category descendant : descendants) {
                if (descendant.parentCategory != null) {
                    CategoryDTO parentDto = dtoMap.get(descendant.parentCategory.id);
                    if (parentDto != null) {
                        CategoryDTO childDto = dtoMap.get(descendant.id);
                        if (parentDto.getChildren() == null) {
                            parentDto.setChildren(new ArrayList<>());
                        }
                        parentDto.getChildren().add(childDto);
                    }
                }
            }
            
            categoryTree.add(rootDto);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", categoryTree);
        response.put("total", totalCount);
        response.put("page", page);
        response.put("size", size);
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getCategory(@PathParam("id") Long id) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Category category = categoryService.findById(id, shop);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Category not found"))
                    .build();
        }
        return Response.ok(categoryMapper.toDto(category)).build();
    }

    @POST
    public Response addCategory(@Valid CategoryCreateRequest request) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Category category = categoryService.addCategory(request.getName(), request.getParentId(), shop);
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
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        Category category = categoryService.findById(id, shop);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Category not found"))
                    .build();
        }
        
        category.name = request.getName();
        category.persist();
        
        return Response.ok(categoryMapper.toDto(category)).build();
    }

    @PUT
    @Path("/{id}/parent/{parentId}")
    public Response updateParent(@PathParam("id") Long id, @PathParam("parentId") Long newParentId) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        try {
            categoryService.updateParent(id, newParentId, shop);
            Category category = categoryService.findById(id, shop);
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
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        try {
            categoryService.moveSubtree(id, newParentId, shop);
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
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        try {
            categoryService.deleteCategory(id, shop);
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
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        List<Category> categories = categoryService.getDescendants(id, shop);
        List<CategoryDTO> dtos = categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}/ancestors")
    public Response getAncestors(@PathParam("id") Long id) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        List<Category> categories = categoryService.getAncestors(id, shop);
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
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        List<Object[]> categoriesWithDepth = categoryService.getCategoriesWithDepth(id, page, size, shop);
        return Response.ok(categoryMapper.toDtoWithDepth(categoriesWithDepth)).build();
    }
    
    @GET
    @Path("/count-descendants/{id}")
    public Response countDescendants(@PathParam("id") Long id) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        long count = categoryService.countDescendants(id, shop);
        return Response.ok(Map.of("count", count)).build();
    }
}