package org.aicart.store.review.dto;

import jakarta.validation.constraints.*;

public class ProductReviewCreateDTO {

    @NotEmpty(message = "Name is required")
    public String name;

    @NotEmpty(message = "Email is required")
    @Email(message = "Email is required")
    public String email;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    public Integer rating;

    @NotEmpty(message = "Title is required")
    public String title;

    @NotEmpty(message = "Body is required")
    @Size(max = 3000)
    public String body;

    public Boolean recommended;
}
