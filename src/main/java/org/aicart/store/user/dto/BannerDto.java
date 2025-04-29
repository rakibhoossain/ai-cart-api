package org.aicart.store.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Valid
    public List<BannerButtonDto> buttons;

    @Min(0)
    @JsonProperty("sort_order")
    public Integer sortOrder;

    @NotNull
    public Boolean active;

    @JsonProperty("start_date")
    public LocalDateTime startDate;

    @JsonProperty("end_date")
    public LocalDateTime endDate;

    @JsonProperty("created_at")
    public LocalDateTime createdAt;

    @JsonProperty("updated_at")
    public LocalDateTime updatedAt;
}
