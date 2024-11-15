package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.aicart.country.Language;

@Entity(name = "attribute_value_translations")
public class AttributeValueTranslation extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "attribute_value_id", nullable = false)
    public AttributeValue attributeValue;

    @ManyToOne
    public Language language;

    @Column(length = 100, nullable = false)
    public String value;
}
