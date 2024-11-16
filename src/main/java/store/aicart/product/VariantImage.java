package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity(name = "variant_images")
public class VariantImage extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    @Column(nullable = false)
    public String url;

    @Column(columnDefinition = "INT DEFAULT 0")
    public int position;

}
