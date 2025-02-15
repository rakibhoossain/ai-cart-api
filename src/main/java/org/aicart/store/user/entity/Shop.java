package org.aicart.store.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.entity.Country;
import org.aicart.entity.Currency;
import java.util.Set;

@Entity
@Table(name = "shops")
public class Shop extends PanacheEntity {

    @Column(name = "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id", nullable = false)
    public Currency currency;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_country", nullable = false)
    public Country primaryCountry;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "shop_country",
            joinColumns = @JoinColumn(name = "shop_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    public Set<Country> countries;
}
