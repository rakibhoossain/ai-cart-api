package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import org.aicart.country.Language;

@Entity(name = "product_translations")
public class ProductTranslation extends PanacheEntity {

    @ManyToOne
    public Product product;

    @ManyToOne
    public Language language;

    @Column(length = 255, nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;
}
