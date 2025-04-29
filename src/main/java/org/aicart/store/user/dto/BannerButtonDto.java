package org.aicart.store.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BannerButtonDto {

    @NotBlank
    @Size(max = 100)
    public String name;

    @NotBlank
    @Size(max = 50)
    public String variant; // "primary", "secondary", etc.

    @Size(max = 20)
    public String position; // e.g. "left", "right", "center"
}
