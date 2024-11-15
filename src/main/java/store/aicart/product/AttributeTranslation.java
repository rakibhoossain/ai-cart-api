package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import org.aicart.country.Language;

@Entity(name = "attribute_translations")
public class AttributeTranslation extends PanacheEntity {

    @ManyToOne
    public Attribute attribute;

    @ManyToOne
    public Language language;

    @Column(length = 100, nullable = false)
    public String name;
}
