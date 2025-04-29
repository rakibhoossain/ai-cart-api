package org.aicart.store.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class BannerDto {
    public Long id;

    @NotNull
    @JsonProperty("shop_id")
    public Long shopId;

    @NotBlank
    @Size(max = 150)
    public String tag;

    @NotBlank
    @Size(max = 255)
    public String title;

    @Size(max = 255)
    public String description;

    @NotNull
    @JsonProperty("background_type")
    public BannerBackgroundEnum backgroundType;

    @JsonProperty("background_id")
    public Long backgroundId;

    @JsonProperty("poster_id")
    public Long posterId;

    @Size(max = 255)
    @JsonProperty("url")
    public String url;

    @Valid
    public BannerButtonDto button;

    @Min(0)
    @JsonProperty("sort_order")
    public Integer sortOrder;

    @NotNull
    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonProperty("created_at")
    public LocalDateTime createdAt;

    @JsonProperty("updated_at")
    public LocalDateTime updatedAt;
}
