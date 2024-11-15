package store.aicart.product;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/product")
public class ProductResource {

    @GET
    public Long index() {
        return Product.count();
    }


    @GET
    @Path("/products")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Product> products() {
        return Product.listAll();
    }
}
