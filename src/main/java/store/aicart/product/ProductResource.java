package store.aicart.product;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import store.aicart.product.dto.ProductItemDTO;
import java.util.List;
import java.util.Map;

@Path("/product")
public class ProductResource {

    @Inject
    EntityManager entityManager;

    @Inject
    ProductService productService;


    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProductItemDTO> products(@QueryParam("lang") Integer lang) {
        return productService.getPaginateProducts(lang);
    }

    @GET
    @Path("/paginated-with-categories")
    public Response getProductsWithCategories(@QueryParam("page") int page,
                                              @QueryParam("pageSize") int pageSize) {
        List<Map<String, Object>> products = productService.getProductsWithCategories(page, pageSize);
        return Response.ok(products).build();
    }

}
