package store.aicart.product;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import store.aicart.product.entity.Category;

import java.util.List;

@Path("/categories")
public class CategoryResource {

    @Inject
    EntityManager entityManager;

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public List<Category> getCategories()
    {
        return this.entityManager.createQuery(
                        "SELECT cc.ancestor FROM CategoryClosure cc", Category.class)
                .getResultList();
    }
}
