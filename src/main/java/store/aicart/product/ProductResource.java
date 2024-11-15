package store.aicart.product;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/product")
public class ProductResource {

    @GET
    public Long index() {
        return Product.count();
    }
}
