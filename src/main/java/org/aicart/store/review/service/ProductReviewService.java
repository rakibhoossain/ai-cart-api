package org.aicart.store.review.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.aicart.store.customer.entity.Customer;
import org.aicart.store.product.entity.Product;
import org.aicart.store.review.dto.ProductReviewCreateDTO;
import org.aicart.store.review.entity.ProductReview;
import org.aicart.store.review.repository.ProductReviewRepository;
import org.aicart.store.user.entity.Shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@ApplicationScoped
public class ProductReviewService {

    @Inject
    ProductReviewRepository repository;

    @Transactional
    public ProductReview createReview(Shop shop, Long productId, ProductReviewCreateDTO dto, Customer customer) {
        Product product = Product.findById(productId);
        if(product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        ProductReview review = new ProductReview();
        review.product = product;
        review.rating = dto.rating;
        review.title = dto.title;
        review.body = dto.body;
        review.recommended = Boolean.TRUE.equals(dto.recommended);
        review.shop = shop;

        if(customer != null) {
            review.customer = customer;
        } else {
            review.name = dto.name;
            review.email = dto.email;
        }

        repository.persist(review);

        // Update product average rating
        updateProductAverageRating(product);

        return review;
    }

    public List<ProductReview> getReviews(Long productId, int page, int size, String sort) {
        return repository.findByProductId(productId, page, size, sort);
    }

    private void updateProductAverageRating(Product product) {
        long totalReviews = ProductReview.count("product", product);
        if(totalReviews == 0) return;
        Long totalRating = (Long) ProductReview.getEntityManager()
                .createQuery("SELECT SUM(r.rating) FROM product_reviews r WHERE r.product = :product")
                .setParameter("product", product)
                .getSingleResult();
        BigDecimal avg = BigDecimal.valueOf(totalRating)
                .divide(BigDecimal.valueOf(totalReviews), 2, RoundingMode.HALF_UP);
        product.averageRating = avg;
        product.reviewCount = (int) totalReviews;
    }
}
