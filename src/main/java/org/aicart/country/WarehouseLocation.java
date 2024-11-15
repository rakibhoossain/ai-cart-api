package org.aicart.country;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    public Country country;


    public String latitude;

    public String longitude;

    @Column(name = "contact_number", length = 20)
    public String contactNumber;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true; // Default to active
}
