package org.aicart.store.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;

public class TemplateSectionDTO implements Serializable {

    @NotBlank
    public String id;

    @NotBlank
    public String type;

    @NotNull
    public Boolean active = true;

    @NotNull
    public Map<String, Object> data;
}
