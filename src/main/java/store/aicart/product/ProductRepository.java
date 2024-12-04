package store.aicart.product;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import store.aicart.product.dto.ProductItemDTO;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<ProductItemDTO> getPaginateProducts(
            Integer page,
            Integer pageSize,
            Optional<Long> minPrice,
            Optional<Long> maxPrice,
            Optional<String> nameFilter,
            Optional<List<Long>> categoryIds,
            Optional<List<Long>> brandIds
            ) {

        int languageId = 1; // TODO Lang
        int countryId = 1; // TODO country Id

        StringBuilder queryBuilder = new StringBuilder("""
        SELECT
        p.id AS product_id,
        p.name AS product_name,
        p.slug AS slug,
        locale.id AS locale_id,
        locale.name AS locale_name,
        p.sku AS sku,
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
        ) AS categories,
        (
            SELECT jsonb_agg(
                           jsonb_build_object(
                                   'id', pv.id,
                                   'sku', pv.sku,
                                   'stock', (
                                       SELECT SUM(vs.quantity)
                                       FROM variant_stocks vs
                                       WHERE vs.variant_id = pv.id
                                   ),
                                   'price', (
                                       SELECT jsonb_build_object(
                                                      'currency_id', vp.currency_id,
                                                      'price', vp.price,
                                                      'discount', vp.discount,
                                                      'tax_rate', vp.tax_rate
                                              )
                                       FROM variant_prices vp
                                       WHERE vp.variant_id = pv.id AND vp.country_id = :countryId
                                   ),
                                   'images', (
                                       SELECT ARRAY_AGG(vi.url)
                                       FROM variant_images vi
                                       WHERE vi.variant_id = pv.id
                                   ),
                                   'attributes', (
                                       SELECT jsonb_agg(
                                                      jsonb_build_object(
                                                              'attribute_name', a.name,
                                                              'value', av.value,
                                                              'attribute_id', av.attribute_id,
                                                              'value_id', av.id
                                                      )
                                              )
                                       FROM product_variant_value pvv
                                                JOIN attribute_values av
                                                     ON pvv.attribute_value_id = av.id
                                                JOIN attributes a
                                                     ON av.attribute_id = a.id
                                       WHERE pvv.variant_id = pv.id
                                   )
                           )
                   )
            FROM product_variants pv
            WHERE pv.product_id = p.id
        ) AS variants
        FROM products p
                 LEFT JOIN product_translations locale
                           ON p.id = locale.product_id AND locale.language_id = :languageId
        WHERE 1 = 1
        """);


        if (minPrice.isPresent()) {
            queryBuilder.append("""
                AND EXISTS (
                    SELECT 1
                    FROM variant_prices vp
                             JOIN product_variants pv ON vp.variant_id = pv.id
                    WHERE pv.product_id = p.id
                      AND vp.price >= :minPrice
                )
            """);
        }


        if (maxPrice.isPresent()) {
            queryBuilder.append("""
                AND EXISTS (
                    SELECT 1
                    FROM variant_prices vp
                             JOIN product_variants pv ON vp.variant_id = pv.id
                    WHERE pv.product_id = p.id
                      AND vp.price <= :maxPrice
                )
            """);
        }

        if (nameFilter.isPresent()) {
            queryBuilder.append("""
                AND (p.name ILIKE :nameFilter OR locale.name ILIKE :nameFilter)
            """);
        }

        if (categoryIds.isPresent()) {
            queryBuilder.append("""
                AND EXISTS (
                    SELECT 1
                    FROM product_category pc
                             JOIN categories c ON pc.category_id = c.id
                    WHERE pc.product_id = p.id
                      AND c.id = ANY(:categoryIds)
                )
            """);
        }


        queryBuilder.append(" ORDER BY p.id");

        // Create query
        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString(), ProductItemDTO.class);

        // Set parameters
        nativeQuery.setParameter("languageId", languageId);
        nativeQuery.setParameter("countryId", countryId);
        minPrice.ifPresent(price -> nativeQuery.setParameter("minPrice", price));
        maxPrice.ifPresent(price -> nativeQuery.setParameter("maxPrice", price));
        nameFilter.ifPresent(filter -> nativeQuery.setParameter("nameFilter", "%" + filter + "%"));

        if(categoryIds.isPresent())
        {
            Long[] categoryArray = categoryIds.get().toArray(new Long[0]);
            nativeQuery.setParameter("categoryIds", categoryArray);
        }
        return nativeQuery.setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
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
