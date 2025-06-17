package org.aicart.store.review.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.review.entity.ProductReview;

import java.util.List;

@ApplicationScoped
public class ProductReviewRepository implements PanacheRepository<ProductReview> {

    public List<ProductReview> findByProductId(Long productId, int page, int size, String sort) {
        String order = switch (sort) {
            case "newest" -> "createdAt DESC";
            case "oldest" -> "createdAt ASC";
            default -> "createdAt DESC";
        };
        return find("product.id = ?1 ORDER BY " + order, productId)
                .page(page, size)
                .list();
    }

    public long countByProductId(Long productId) {
        return count("product.id", productId);
    }
}
