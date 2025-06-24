package org.aicart.blog.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.aicart.blog.entity.Blog;
import org.aicart.blog.entity.BlogStatus;
import org.aicart.entity.Language;
import org.aicart.store.user.entity.Shop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class BlogRepository implements PanacheRepository<Blog> {
    
    @PersistenceContext
    EntityManager em;
    
    public List<Blog> findByShop(Shop shop, int page, int size, String sortField, boolean ascending, String searchQuery, Language language) {
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT DISTINCT b FROM Blog b JOIN b.translations t " +
            "WHERE b.shop = :shop AND t.language = :language");
        
        Map<String, Object> params = new HashMap<>();
        params.put("shop", shop);
        params.put("language", language);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(" AND (t.title ILIKE :search OR t.content ILIKE :search)");
            params.put("search", "%" + searchQuery.trim() + "%");
        }
        
        String orderBy;
        if ("title".equals(sortField)) {
            orderBy = "t.title";
        } else if (sortField.startsWith("translation.")) {
            orderBy = "t." + sortField.substring(12);
        } else {
            orderBy = "b." + sortField;
        }
        
        queryBuilder.append(" ORDER BY ").append(orderBy).append(" ").append(ascending ? "ASC" : "DESC");
        
        TypedQuery<Blog> query = em.createQuery(queryBuilder.toString(), Blog.class);
        
        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        
        return query
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
    }
    
    public long countByShop(Shop shop, String searchQuery, Language language) {
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT COUNT(DISTINCT b) FROM Blog b JOIN b.translations t " +
            "WHERE b.shop = :shop AND t.language = :language");
        
        Map<String, Object> params = new HashMap<>();
        params.put("shop", shop);
        params.put("language", language);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            queryBuilder.append(" AND (t.title ILIKE :search OR t.content ILIKE :search)");
            params.put("search", "%" + searchQuery.trim() + "%");
        }
        
        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);
        
        // Set parameters
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        
        return query.getSingleResult();
    }
    
    public Optional<Blog> findBySlug(String slug, Shop shop) {
        return find("slug = ?1 AND shop = ?2", slug, shop).firstResultOptional();
    }
    
    public List<Blog> findPublishedByShop(Shop shop, int page, int size, Language language) {
        return em.createQuery(
                "SELECT DISTINCT b FROM Blog b JOIN b.translations t " +
                "WHERE b.shop = :shop AND b.status = :status AND t.language = :language " +
                "ORDER BY b.publishedAt DESC", 
                Blog.class)
                .setParameter("shop", shop)
                .setParameter("status", BlogStatus.PUBLISHED)
                .setParameter("language", language)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
    
    public List<Blog> findByCategory(Long categoryId, Shop shop, int page, int size, Language language) {
        return em.createQuery(
                "SELECT DISTINCT b FROM Blog b JOIN b.categories c JOIN b.translations t " +
                "WHERE c.id = :categoryId AND b.shop = :shop AND b.status = :status AND t.language = :language " +
                "ORDER BY b.publishedAt DESC", 
                Blog.class)
                .setParameter("categoryId", categoryId)
                .setParameter("shop", shop)
                .setParameter("status", BlogStatus.PUBLISHED)
                .setParameter("language", language)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
    
    public List<Blog> findByTag(Long tagId, Shop shop, int page, int size, Language language) {
        return em.createQuery(
                "SELECT DISTINCT b FROM Blog b JOIN b.tags t JOIN b.translations tr " +
                "WHERE t.id = :tagId AND b.shop = :shop AND b.status = :status AND tr.language = :language " +
                "ORDER BY b.publishedAt DESC", 
                Blog.class)
                .setParameter("tagId", tagId)
                .setParameter("shop", shop)
                .setParameter("status", BlogStatus.PUBLISHED)
                .setParameter("language", language)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }
    
    public long countByCategory(Long categoryId, Shop shop) {
        return em.createQuery(
                "SELECT COUNT(DISTINCT b) FROM Blog b JOIN b.categories c " +
                "WHERE c.id = :categoryId AND b.shop = :shop AND b.status = :status", 
                Long.class)
                .setParameter("categoryId", categoryId)
                .setParameter("shop", shop)
                .setParameter("status", BlogStatus.PUBLISHED)
                .getSingleResult();
    }
    
    public long countByTag(Long tagId, Shop shop) {
        return em.createQuery(
                "SELECT COUNT(DISTINCT b) FROM Blog b JOIN b.tags t " +
                "WHERE t.id = :tagId AND b.shop = :shop AND b.status = :status", 
                Long.class)
                .setParameter("tagId", tagId)
                .setParameter("shop", shop)
                .setParameter("status", BlogStatus.PUBLISHED)
                .getSingleResult();
    }
}