package store.aicart.product;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import store.aicart.product.dto.ProductItemDTO;

import java.util.List;

@ApplicationScoped
public class ProductRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<ProductItemDTO> getPaginateProducts(Integer lang) {
        int preferredLanguageId = lang != null ? lang : 1;
        int defaultLanguageId = 1;

        String queryStr = """

                SELECT
    p.id AS product_id,
    COALESCE(pt1.name, pt2.name) AS product_name,
    (
        SELECT jsonb_agg(
                       jsonb_build_object(
                               'id', c.id,
                               'name', c.name,
                               'category_id', pc.category_id,
                                'depth', cc.depth
                       )
               )
        FROM product_category pc
                 JOIN category_closure cc
                      ON pc.category_id = cc.descendant_id
                 JOIN categories c
                      ON cc.ancestor_id = c.id
        WHERE pc.product_id = p.id
    ) AS categories
FROM products p
         LEFT JOIN product_translations pt1
                   ON p.id = pt1.product_id AND pt1.language_id = :preferredLanguageId
         LEFT JOIN product_translations pt2
                   ON p.id = pt2.product_id AND pt2.language_id = :defaultLanguageId
ORDER BY p.id
                """;

        List<ProductItemDTO> results = entityManager.createNativeQuery(queryStr, ProductItemDTO.class)
                .setParameter("defaultLanguageId", defaultLanguageId)
                .setParameter("preferredLanguageId", preferredLanguageId)
                .getResultList();

            return results;
        }





    public List<Product> findPaginatedProducts(int page, int pageSize) {
        return entityManager.createQuery(
                        "SELECT p FROM products p ORDER BY p.id", Product.class)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public List<Object[]> findCategoriesByProductIds(List<Long> productIds) {

        String sql = """
            SELECT 
                p.id AS productId,
                c1.category_id AS categoryId,
                a1.id AS ancestorId,
                a1.name AS categoryName,
                cc1.depth AS depth
            FROM 
                products p
            JOIN 
                product_category c1 ON p.id = c1.product_id
            LEFT JOIN 
                category_closure cc1 ON c1.category_id = cc1.descendant_id
            LEFT JOIN 
                categories a1 ON cc1.ancestor_id = a1.id
            WHERE 
                p.id IN (:productIds)
            ORDER BY 
                p.id, a1.id
        """;

        return entityManager.createNativeQuery(sql)
                .setParameter("productIds", productIds) // Pass the product IDs
                .getResultList();
    }
}
