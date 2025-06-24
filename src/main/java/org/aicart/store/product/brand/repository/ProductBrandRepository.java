package org.aicart.store.product.brand.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.product.entity.ProductBrand;
import org.aicart.store.user.entity.Shop;

import java.util.List;

@ApplicationScoped
public class ProductBrandRepository implements PanacheRepository<ProductBrand> {
    
    /**
     * Find a brand by ID and shop
     */
    public ProductBrand findByIdAndShop(Long id, Shop shop) {
        return find("id = ?1 and shop = ?2", id, shop).firstResult();
    }
    
    /**
     * Get brands with pagination, sorting, and optional search
     */
    public List<ProductBrand> getBrands(int page, int size, String sortField, boolean ascending, String searchQuery, Shop shop) {
        String direction = ascending ? "ASC" : "DESC";
        String query = "shop = ?1";
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query += " AND name ILIKE ?2";
            return find(query + " ORDER BY " + sortField + " " + direction, shop, "%" + searchQuery.trim() + "%")
                    .page(page, size)
                    .list();
        } else {
            return find(query + " ORDER BY " + sortField + " " + direction, shop)
                    .page(page, size)
                    .list();
        }
    }
    
    /**
     * Count total number of brands with optional search
     */
    public long countBrands(String searchQuery, Shop shop) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return count("shop = ?1 AND name ILIKE ?2", shop, "%" + searchQuery.trim() + "%");
        } else {
            return count("shop = ?1", shop);
        }
    }
}