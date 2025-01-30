package org.aicart.store.product.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CategoryTranslationId implements Serializable {
    public Long categoryId;
    public Long languageId;

    public CategoryTranslationId() {}

    public CategoryTranslationId(Long categoryId, Long languageId) {
        this.categoryId = categoryId;
        this.languageId = languageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryTranslationId)) return false;
        CategoryTranslationId that = (CategoryTranslationId) o;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(languageId, that.languageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, languageId);
    }
}

