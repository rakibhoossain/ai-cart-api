package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;
import org.aicart.media.entity.FileStorage;
import org.aicart.util.StringSlugifier;
import java.util.Set;

@Entity
@Table(name = "shops")
public class Shop extends PanacheEntity {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "tag_line")
    public String tagLine;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_country", nullable = false)
    public Country primaryCountry;

    @Column(nullable = false, unique = true)
    public String host;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "shop_country",
            joinColumns = @JoinColumn(name = "shop_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    public Set<Country> countries;

    @PrePersist
    public void generateUniqueSlugAndUpdateTimestamp() {
        if (this.host == null || this.host.isEmpty()) {
            final String baseSlug = StringSlugifier.slugify(this.name);
            String uniqueSlug = baseSlug;
            int counter = 1;

            while (Shop.find("host", uniqueSlug).firstResult() != null) {
                uniqueSlug = baseSlug + "-" + counter++;
            }

            this.host = uniqueSlug;
        }
    }
}
