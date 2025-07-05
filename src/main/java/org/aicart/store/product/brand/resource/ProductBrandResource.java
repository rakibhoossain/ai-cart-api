package org.aicart.store.product.brand.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.brand.dto.ProductBrandDTO;
import org.aicart.store.product.brand.mapper.ProductBrandMapper;
import org.aicart.store.product.brand.service.ProductBrandService;
import org.aicart.store.product.entity.ProductBrand;
import org.aicart.store.user.entity.Shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/brands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductBrandResource {

    @Inject
    ProductBrandService brandService;
    
    private Long getShopId() {
        // In a real application, this would come from authentication context
        return 1L;
    }
    
    @GET
    public Response getBrands(
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
        
        List<ProductBrand> brands = brandService.getBrands(page, size, sortField, 
                "asc".equalsIgnoreCase(sortOrder), searchQuery, shop);
        long totalCount = brandService.countBrands(searchQuery, shop);
        
        List<ProductBrandDTO> dtos = brands.stream()
                .map(ProductBrandMapper::toDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", dtos);
        response.put("total", totalCount);
        response.put("page", page);
        response.put("size", size);
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getBrand(@PathParam("id") Long id) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        ProductBrand brand = brandService.findById(id, shop);
        if (brand == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Brand not found"))
                    .build();
        }
        return Response.ok(ProductBrandMapper.toDto(brand)).build();
    }

    @POST
    public Response createBrand(@Valid ProductBrandDTO dto) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        ProductBrand brand = brandService.createBrand(
            dto.getName(),
            dto.getDescription(),
            dto.getLogo(),
            dto.getWebsite(),
            shop
        );
        return Response.status(Response.Status.CREATED)
                .entity(ProductBrandMapper.toDto(brand))
                .build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateBrand(
            @PathParam("id") Long id, 
            @Valid ProductBrandDTO dto
    ) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        try {
            ProductBrand brand = brandService.updateBrand(
                id,
                dto.getName(),
                dto.getDescription(),
                dto.getLogo(),
                dto.getWebsite(),
                shop
            );
            return Response.ok(ProductBrandMapper.toDto(brand)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBrand(@PathParam("id") Long id) {
        Shop shop = Shop.findById(getShopId());
        if (shop == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("message", "Shop not found"))
                    .build();
        }
        
        try {
            brandService.deleteBrand(id, shop);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }
}