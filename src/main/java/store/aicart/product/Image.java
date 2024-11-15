package store.aicart.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "images")
public class Image extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    public ProductVariant productVariant;

    @Column(nullable = false)
    public String url;

    @Column(columnDefinition = "INT DEFAULT 0")
    public int position;

}
