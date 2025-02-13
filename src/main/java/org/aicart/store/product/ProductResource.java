package org.aicart.store.product;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.aicart.store.product.dto.product.ProductCreateRequestDTO;
import org.aicart.util.QueryParamConverter;
import org.aicart.store.product.dto.ProductItemDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/product")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    EntityManager entityManager;

    @Inject
    ProductService productService;

    @Inject
    ProductStoreService productCreateService;


    @GET
    @Path("/")
    public List<ProductItemDTO> products(
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("minPrice") Optional<Long> minPrice,
            @QueryParam("maxPrice") Optional<Long> maxPrice,
            @QueryParam("q") Optional<String> nameFilter,
            @QueryParam("categoryIds") String categoryIds,
            @QueryParam("brandIds") String brandIds
    ) {
        Optional<List<Long>> categoryList = Optional.ofNullable(categoryIds).map(QueryParamConverter::toLongList);
        Optional<List<Long>> brandList = Optional.ofNullable(brandIds).map(QueryParamConverter::toLongList);

        Integer pageNumber = (page == null) ? 0 : page;
        Integer pageLimit = (pageSize == null) ? 20 : pageSize;

        return productService.getPaginateProducts(pageNumber, pageLimit, minPrice, maxPrice, nameFilter, categoryList, brandList);
    }

    @GET
    @Path("/detail/{slug}")
    public Response productDetail(@PathParam("slug") String slug) {

        ProductItemDTO product = productService.getProductBySlug(slug);
        if(product == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No products found.")
                    .build();
        }

        return Response.ok(product).build();
    }


    @POST
    @Path("/")
    public Response productCreate(ProductCreateRequestDTO productDTO) {
        return productCreateService.productCreate(productDTO);
    }


    @PUT
    @Path("/update/{productId}")
    public Response productUpdate(@PathParam("productId") BigInteger productId, ProductCreateRequestDTO productDTO) {
        return productCreateService.productUpdate(productId, productDTO);
    }

    @GET
    @Path("/paginated-with-categories")
    public Response getProductsWithCategories(@QueryParam("page") int page,
                                              @QueryParam("pageSize") int pageSize) {
        List<Map<String, Object>> products = productService.getProductsWithCategories(page, pageSize);
        return Response.ok(products).build();
    }

}
