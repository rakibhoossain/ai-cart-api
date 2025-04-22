package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.ProductCollection;
import org.aicart.store.product.entity.ProductCollectionCondition;
import org.aicart.store.product.process.SmartCollectionQueryBuilder;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class ProductCollectionRepository implements PanacheRepository<ProductCollection> {

    private static final Logger LOG = Logger.getLogger(ProductCollectionRepository.class);
    private static final int BATCH_SIZE = 1000;

    @Inject
    EntityManager em;

    @Inject
    SmartCollectionQueryBuilder queryBuilder;

    @Transactional
    public void updateSmartCollection(Long collectionId) {
        LOG.infof("Updating smart collection %d", collectionId);

        // 1. Delete existing mappings in batches
        clearExistingMappings(collectionId);

        // 2. Process matching products in batches
        int offset = 0;
        int totalProcessed = 0;
        List<Long> productIds;

        do {
            productIds = findMatchingProductsBatch(collectionId, offset);
            if (!productIds.isEmpty()) {
                insertBatchMappings(collectionId, productIds);
                totalProcessed += productIds.size();
                offset += BATCH_SIZE;
                LOG.infof("Processed batch: %d (Total: %d)", productIds.size(), totalProcessed);
            }
        } while (!productIds.isEmpty());

        LOG.infof("Finished updating collection %d. Total products: %d", collectionId, totalProcessed);
    }

    private List<Long> findMatchingProductsBatch(Long collectionId, int offset) {
        var collection = findById(collectionId);
        var conditions = em.createQuery(
                        "SELECT c FROM ProductCollectionCondition c WHERE c.collection.id = :collectionId",
                        ProductCollectionCondition.class)
                .setParameter("collectionId", collectionId)
                .getResultList();

        var result = queryBuilder.buildQuery(
                collectionId,
                collection.conditionMatch,
                conditions
//                conditions.stream()
//                        .map(c -> new ProductCollectionCondition(c.getFieldName(), c.getOperator(), c.getValue()))
//                        .toList()
        );

        var query = em.createNativeQuery(result.query);
        result.params.forEach(query::setParameter);

        return query.getResultList();
    }

    private void clearExistingMappings(Long collectionId) {
        int deleted;
        do {
            deleted = em.createNativeQuery("""
                DELETE FROM product_collection_pivot
                WHERE collection_id = :collectionId
                AND product_id IN (
                    SELECT product_id FROM product_collection_pivot
                    WHERE collection_id = :collectionId
                    LIMIT :batchSize
                )
                """)
                    .setParameter("collectionId", collectionId)
                    .setParameter("batchSize", BATCH_SIZE * 5)
                    .executeUpdate();
            em.flush();
        } while (deleted > 0);
    }

    private void insertBatchMappings(Long collectionId, List<Long> productIds) {
        var queryStr = new StringBuilder("INSERT INTO product_collection_pivot (collection_id, product_id) VALUES ");
        var values = new ArrayList<String>();
        var params = new HashMap<String, Object>();

        params.put("collectionId", collectionId);

        for (int i = 0; i < productIds.size(); i++) {
            values.add("(:collectionId, :productId" + i + ")");
            params.put("productId" + i, productIds.get(i));
        }

        queryStr.append(String.join(",", values));

        Query query = em.createNativeQuery(queryStr.toString());

        // Bind all parameters
        params.forEach(query::setParameter);

        query.executeUpdate(); // For INSERT/UPDATE/DELETE
    }
}
