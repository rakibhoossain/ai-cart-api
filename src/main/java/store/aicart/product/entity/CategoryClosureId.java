package store.aicart.product.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CategoryClosureId implements Serializable {
    public Long ancestorId;
    public Long descendantId;

    public CategoryClosureId() {}

    public CategoryClosureId(Long ancestorId, Long descendantId) {
        this.ancestorId = ancestorId;
        this.descendantId = descendantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryClosureId)) return false;
        CategoryClosureId that = (CategoryClosureId) o;
        return Objects.equals(ancestorId, that.ancestorId) &&
                Objects.equals(descendantId, that.descendantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ancestorId, descendantId);
    }
}

