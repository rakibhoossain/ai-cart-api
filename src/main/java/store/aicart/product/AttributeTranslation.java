package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import org.aicart.country.Language;

@Entity(name = "attribute_translations")
public class AttributeTranslation extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    public Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY)
    public Language language;

    @Column(length = 100, nullable = false)
    public String name;
}
