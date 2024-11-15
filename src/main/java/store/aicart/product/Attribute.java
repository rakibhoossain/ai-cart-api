package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

/**
 * Defines an attribute type like size or color
 */
@Entity(name = "attributes")
public class Attribute extends PanacheEntity {

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL)
    public List<AttributeTranslation> translations;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL)
    public List<AttributeValue> values;
}
