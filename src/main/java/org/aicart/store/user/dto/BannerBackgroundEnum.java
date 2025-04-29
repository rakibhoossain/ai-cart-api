package org.aicart.store.user.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BannerBackgroundEnum {
    IMAGE("image"),
    VIDEO("video");

    private final String value;

    BannerBackgroundEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
