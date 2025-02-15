package org.aicart.entity;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.aicart.store.user.entity.Shop;

import java.util.Set;

@Entity(name = "warehouse_locations")
public class WarehouseLocation extends PanacheEntity {

    @Column(nullable = false)
    public String name; // Name of the warehouse

    @Column(name = "address_line1", nullable = false)
    public String addressLine1;

    @Column(name = "address_line2")
    public String addressLine2;

    @Column(nullable = false)
    public String city;

    @Column
    public String state;

    @Column(name = "postal_code", nullable = false)
    public String postalCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    public Country country;

    public String latitude;

    public String longitude;

    @Column(name = "contact_number", length = 20)
    public String contactNumber;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true; // Default to active

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "warehouse_sell_country",
            joinColumns = @JoinColumn(name = "warehouse_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    public Set<Country> sellCountry;
}
