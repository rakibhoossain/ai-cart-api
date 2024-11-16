package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

/**
 * Stores values for each attribute, e.g., "Red" or "Medium."
 */
@Entity(name = "attribute_values")
public class AttributeValue extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    public Attribute attribute;

    @OneToMany(mappedBy = "attributeValue", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    public List<AttributeValueTranslation> translations;
}
