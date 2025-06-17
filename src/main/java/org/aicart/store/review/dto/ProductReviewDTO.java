package org.aicart.store.review.dto;

import org.aicart.store.review.entity.ProductReview;

import java.time.LocalDateTime;

public class ProductReviewDTO {
    public Long id;
    public int rating;
    public String title;
    public String body;
    public Boolean recommended;
    public String reviewerName;
    public Boolean verifiedBuyer;
    public LocalDateTime createdAt;

    public static ProductReviewDTO fromEntity(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.id = review.id;
        dto.rating = review.rating;
        dto.title = review.title;
        dto.body = review.body;
        dto.recommended = review.recommended;
        dto.reviewerName = review.name;
        dto.verifiedBuyer = review.customer != null;
        dto.createdAt = review.createdAt;
        return dto;
    }
}
