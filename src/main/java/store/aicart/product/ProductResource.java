package store.aicart.product;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import store.aicart.product.dto.ProductItemDTO;
import java.util.List;

@Path("/product")
public class ProductResource {

    @Inject
    EntityManager entityManager;

    @GET
    public Long index() {
        Product product = Product.find("SELECT p FROM products p JOIN FETCH p.translations WHERE p.id = ?1", 1).firstResult();
        Log.info(product);
        return Product.count();
    }


    @GET
    @Path("/products")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProductItemDTO> products(@QueryParam("lang") Integer lang) {


        int preferredLanguageId = lang != null ? lang : 1;
        int defaultLanguageId = 1;

        String queryStr = """
                SELECT p.id,
                       COALESCE(t1.name, t2.name) AS name,
                       (SELECT jsonb_agg(
                                       jsonb_build_object(
                                               'id', c.id,
                                               'name', COALESCE(ct1.name, ct2.name),
                                               'children', (
                                                   SELECT jsonb_agg(
                                                                  jsonb_build_object(
                                                                          'id', child.id,
                                                                          'name', COALESCE(child_ct1.name, child_ct2.name)
                                                                  )
                                                          )
                                                   FROM categories child
                                                            LEFT JOIN category_translations child_ct1
                                                                      ON child.id = child_ct1.category_id
                                                                          AND child_ct1.language_id = :preferredLanguageId
                                                            LEFT JOIN category_translations child_ct2
                                                                      ON child.id = child_ct2.category_id
                                                                          AND child_ct2.language_id = :defaultLanguageId
                                                   WHERE child.parent_category_id = c.id
          
                                               )
                                       )
                               ) AS categories
                        FROM categories c
                                 LEFT JOIN category_translations ct1
                                           ON c.id = ct1.category_id
                                               AND ct1.language_id = :preferredLanguageId
                                 LEFT JOIN category_translations ct2
                                           ON c.id = ct2.category_id
                                               AND ct2.language_id = :defaultLanguageId) AS categories
                FROM products p
                         LEFT JOIN product_translations t1
                                   ON p.id = t1.product_id AND t1.language_id = :preferredLanguageId
                         LEFT JOIN product_translations t2
                                   ON p.id = t2.product_id AND t2.language_id = :defaultLanguageId
                """;

        List<ProductItemDTO> results = entityManager.createNativeQuery(queryStr, ProductItemDTO.class)
                .setParameter("defaultLanguageId", defaultLanguageId)
                .setParameter("preferredLanguageId", preferredLanguageId)
                .getResultList();

        return results;


//        return entityManager.createQuery(
//                        """
//                        SELECT new store.aicart.product.dto.ProductItemDTO(
//                            p.id,
//                            COALESCE(t1.name, t2.name) AS name
//                        )
//                        FROM products p
//                        LEFT JOIN product_translations t1
//                            ON p.id = t1.product.id AND t1.language.id = :preferredLanguageId
//                        LEFT JOIN product_translations t2
//                            ON p.id = t2.product.id AND t2.language.id = :defaultLanguageId
//                        ORDER BY p.id
//                        """, ProductItemDTO.class)
//                .setParameter("preferredLanguageId", preferredLanguageId)
//                .setParameter("defaultLanguageId", defaultLanguageId)
//                .getResultList();
    }

}
