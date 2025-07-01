package org.aicart.media;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.aicart.media.entity.FileStorage;
import org.aicart.store.user.entity.Shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MediaRepository implements PanacheRepository<FileStorage> {

    @PersistenceContext
    EntityManager em;

    public List<FileStorage> findWithFilters(Shop shop, String search, String fileType, int page, int size, String sortBy, String order) {
        StringBuilder queryBuilder = new StringBuilder("SELECT f FROM FileStorage f WHERE shop.id=:shopId");
        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shop.id);

        // Add search filter
        if (search != null && !search.trim().isEmpty()) {
            queryBuilder.append(" AND (LOWER(f.fileName) LIKE :search OR LOWER(f.altText) LIKE :search OR LOWER(f.mimeType) LIKE :search)");
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        // Add file type filter
        if (fileType != null && !fileType.trim().isEmpty() && !"all".equals(fileType)) {
            queryBuilder.append(" AND f.fileType = :fileType");
            params.put("fileType", fileType);
        }

        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String direction = "desc".equalsIgnoreCase(order) ? "DESC" : "ASC";
            queryBuilder.append(" ORDER BY f.").append(sortBy).append(" ").append(direction);
        } else {
            queryBuilder.append(" ORDER BY f.createdAt DESC");
        }

        TypedQuery<FileStorage> query = em.createQuery(queryBuilder.toString(), FileStorage.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countWithFilters(Shop shop, String search, String fileType) {
        StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(f) FROM FileStorage f WHERE shop.id=:shopId");
        Map<String, Object> params = new HashMap<>();
        params.put("shopId", shop.id);

        // Add search filter
        if (search != null && !search.trim().isEmpty()) {
            queryBuilder.append(" AND (LOWER(f.fileName) LIKE :search OR LOWER(f.altText) LIKE :search OR LOWER(f.mimeType) LIKE :search)");
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        // Add file type filter
        if (fileType != null && !fileType.trim().isEmpty() && !"all".equals(fileType)) {
            queryBuilder.append(" AND f.fileType = :fileType");
            params.put("fileType", fileType);
        }

        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);

        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getSingleResult();
    }

    /**
     * Find a file by ID that belongs to the specified shop
     */
    public FileStorage findByIdAndShop(Long id, Shop shop) {
        return find("id = ?1 and shop.id = ?2", id, shop.id).firstResult();
    }

    /**
     * Check if a file exists and belongs to the specified shop
     */
    public boolean existsByIdAndShop(Long id, Shop shop) {
        return count("id = ?1 and shop.id = ?2", id, shop.id) > 0;
    }
}
