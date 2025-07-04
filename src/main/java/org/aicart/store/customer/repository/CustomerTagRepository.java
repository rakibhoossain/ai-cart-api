package org.aicart.store.customer.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.store.customer.entity.CustomerTag;
import org.aicart.store.user.entity.Shop;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerTagRepository implements PanacheRepository<CustomerTag> {
    
    public List<CustomerTag> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
        String query = "shop = ?1";
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query += " AND name ILIKE ?2";
            return find(query, Sort.by(sortField).direction(ascending ? Sort.Direction.Ascending : Sort.Direction.Descending), 
                    shop, "%" + searchQuery.trim() + "%")
                    .page(Page.of(page, size))
                    .list();
        } else {
            return find(query, Sort.by(sortField).direction(ascending ? Sort.Direction.Ascending : Sort.Direction.Descending), shop)
                    .page(Page.of(page, size))
                    .list();
        }
    }
    
    public long countByShop(Shop shop, String searchQuery) {
        String query = "shop = ?1";
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query += " AND name ILIKE ?2";
            return count(query, shop, "%" + searchQuery.trim() + "%");
        } else {
            return count(query, shop);
        }
    }
    
    public List<CustomerTag> findAllByShop(Shop shop) {
        return find("shop", shop).list();
    }
    
    public Optional<CustomerTag> findByName(String name, Shop shop) {
        return find("name = ?1 AND shop = ?2", name, shop).firstResultOptional();
    }
    
    public Optional<CustomerTag> findBySlug(String slug, Shop shop) {
        return find("slug = ?1 AND shop = ?2", slug, shop).firstResultOptional();
    }
    
    public List<CustomerTag> findByShopWithCustomerCounts(Shop shop) {
        return find("shop = ?1 ORDER BY name", shop).list();
    }
    
    public long countCustomersWithTag(CustomerTag tag) {
        return tag.customers.size();
    }
    
    public boolean existsByNameAndShop(String name, Shop shop) {
        return count("name = ?1 AND shop = ?2", name, shop) > 0;
    }
    
    public boolean existsBySlugAndShop(String slug, Shop shop) {
        return count("slug = ?1 AND shop = ?2", slug, shop) > 0;
    }
    
    public boolean existsByNameAndShopExcludingId(String name, Shop shop, Long excludeId) {
        return count("name = ?1 AND shop = ?2 AND id != ?3", name, shop, excludeId) > 0;
    }
    
    public boolean existsBySlugAndShopExcludingId(String slug, Shop shop, Long excludeId) {
        return count("slug = ?1 AND shop = ?2 AND id != ?3", slug, shop, excludeId) > 0;
    }
}
