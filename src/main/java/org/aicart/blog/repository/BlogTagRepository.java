package org.aicart.blog.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.aicart.blog.entity.BlogTag;
import org.aicart.store.user.entity.Shop;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BlogTagRepository implements PanacheRepository<BlogTag> {
    
    public List<BlogTag> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery) {
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
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return count("shop = ?1 AND name ILIKE ?2", shop, "%" + searchQuery.trim() + "%");
        } else {
            return count("shop = ?1", shop);
        }
    }
    
    public Optional<BlogTag> findByName(String name, Shop shop) {
        return find("name = ?1 AND shop = ?2", name, shop).firstResultOptional();
    }
    
    public Optional<BlogTag> findBySlug(String slug, Shop shop) {
        return find("slug = ?1 AND shop = ?2", slug, shop).firstResultOptional();
    }
}