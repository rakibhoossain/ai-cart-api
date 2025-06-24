package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.Category;
import org.aicart.store.product.entity.CategoryClosure;
import org.aicart.store.product.entity.CategoryClosureId;
import org.aicart.store.user.entity.Shop;

import java.util.List;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {
    
    @Inject
    EntityManager em;
    
    /**
     * Find a category by ID
     */
    public Category findById(Long id, Shop shop) {
        return find("id = ?1 and shop = ?2", id, shop).firstResult();
    }

    /**
     * Get categories with pagination, sorting, and optional search
     */
    public List<Category> getCategories(int page, int size, String sortField, boolean ascending, String searchQuery, Shop shop) {
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
     * Count total number of categories with optional search
     */
    public long countCategories(String searchQuery, Shop shop) {
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            return count("shop = ?1 AND name ILIKE ?2", shop, "%" + searchQuery.trim() + "%");
        } else {
            return count("shop = ?1", shop);
        }
    }

    @Transactional
    public Category addCategory(String name, Long parentId, Shop shop) {
        Category category = new Category();
        category.name = name;
        category.shop = shop;

        if (parentId != null) {
            Category parent = findById(parentId, shop);
            if (parent == null) {
                throw new IllegalArgumentException("Parent category not found");
            }
            category.parentCategory = parent;
        }

        category.persist();
        addToClosureTable(category, category.parentCategory);
        return category;
    }

    @Transactional
    public void updateParent(Long categoryId, Long newParentId, Shop shop) {
        Category category = findById(categoryId, shop);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        Category newParent = newParentId != null ? findById(newParentId, shop) : null;
        if (newParentId != null && newParent == null) {
            throw new IllegalArgumentException("New parent category not found");
        }

        category.parentCategory = newParent;
        category.persist();

        // Clear old closure table relationships
        em.createQuery("DELETE FROM CategoryClosure WHERE descendant.id IN " +
                "(SELECT descendant.id FROM CategoryClosure WHERE ancestor.id = :categoryId)")
                .setParameter("categoryId", categoryId)
                .executeUpdate();

        // Rebuild closure table for the new hierarchy
        addToClosureTable(category, newParent);
    }

    @Transactional
    public void deleteCategory(Long id, Shop shop) {
        Category category = findById(id, shop);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Reassign children to the deleted category's parent
        List<Category> children = list("parentCategory.id = ?1 AND shop = ?2", id, shop);
        for (Category child : children) {
            child.parentCategory = category.parentCategory;
            child.persist();
        }

        CategoryClosure.delete("descendant.id", id);
        delete("id = ?1 AND shop = ?2", id, shop);
    }

    public List<Category> getDescendants(Long categoryId, Shop shop) {
        return em.createQuery(
                "SELECT cc.descendant FROM CategoryClosure cc " +
                "WHERE cc.ancestor.id = :id AND cc.descendant.id != :id " +
                "AND cc.descendant.shop = :shop", Category.class)
                .setParameter("id", categoryId)
                .setParameter("shop", shop)
                .getResultList();
    }

    public List<Category> getAncestors(Long categoryId, Shop shop) {
        return em.createQuery(
                "SELECT cc.ancestor FROM CategoryClosure cc " +
                "WHERE cc.descendant.id = :id AND cc.ancestor.id != :id " +
                "AND cc.ancestor.shop = :shop", Category.class)
                .setParameter("id", categoryId)
                .setParameter("shop", shop)
                .getResultList();
    }

    @Transactional
    public void moveSubtree(Long categoryId, Long newParentId, Shop shop) {
        Category category = findById(categoryId, shop);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        Category newParent = newParentId != null ? findById(newParentId, shop) : null;
        if (newParentId != null && newParent == null) {
            throw new IllegalArgumentException("New parent category not found");
        }
        
        // Update the parent reference
        category.parentCategory = newParent;
        category.persist();
        
        // Use a more efficient approach for updating the closure table
        // This uses a single DELETE and a single INSERT with subqueries
        
        // 1. Delete all paths that go through the subtree root
        em.createNativeQuery(
            "DELETE FROM category_closure " +
            "WHERE descendant_id IN (SELECT descendant_id FROM category_closure WHERE ancestor_id = :categoryId) " +
            "AND ancestor_id IN (SELECT ancestor_id FROM category_closure WHERE descendant_id = :categoryId " +
            "AND ancestor_id != descendant_id)")
            .setParameter("categoryId", categoryId)
            .executeUpdate();
            
        // 2. Insert new paths
        if (newParentId != null) {
            em.createNativeQuery(
                "INSERT INTO category_closure (ancestor_id, descendant_id, depth) " +
                "SELECT a.ancestor_id, d.descendant_id, a.depth + d.depth + 1 " +
                "FROM category_closure a " +
                "CROSS JOIN category_closure d " +
                "WHERE a.descendant_id = :newParentId " +
                "AND d.ancestor_id = :categoryId")
                .setParameter("newParentId", newParentId)
                .setParameter("categoryId", categoryId)
                .executeUpdate();
        }
    }

    /**
     * Get entire category tree for a specific shop with pagination and search
     */
    public List<Object[]> getEntireCategoryTree(Shop shop, int page, int size, String searchQuery) {
        String baseQuery = 
            "SELECT c, p, cc.depth FROM Category c " +
            "LEFT JOIN c.parentCategory p " +
            "JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
            "WHERE c.shop = :shop ";
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            baseQuery += "AND c.name ILIKE :searchQuery ";
        }
        
        baseQuery += "ORDER BY cc.depth, c.name";
        
        TypedQuery<Object[]> query = em.createQuery(baseQuery, Object[].class)
            .setParameter("shop", shop)
            .setFirstResult(page * size)
            .setMaxResults(size);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("searchQuery", "%" + searchQuery.trim() + "%");
        }
        
        return query.getResultList();
    }

    /**
     * Count total categories in tree with optional search filter
     */
    public long countCategoryTree(Shop shop, String searchQuery) {
        String baseQuery = 
            "SELECT COUNT(c) FROM Category c " +
            "WHERE c.shop = :shop ";
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            baseQuery += "AND c.name LIKE :searchQuery ";
        }
        
        TypedQuery<Long> query = em.createQuery(baseQuery, Long.class)
            .setParameter("shop", shop);
        
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            query.setParameter("searchQuery", "%" + searchQuery.trim() + "%");
        }
        
        return query.getSingleResult();
    }

    public List<Object[]> getCategoriesWithDepth(Long parentId, int page, int size, Shop shop) {
        String query;
        if (parentId == null) {
            query = "SELECT c, cc.depth FROM Category c JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
                    "WHERE cc.ancestor.id = cc.descendant.id AND c.parentCategory IS NULL AND c.shop = :shop " +
                    "ORDER BY c.name";
            
            return em.createQuery(query, Object[].class)
                .setParameter("shop", shop)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        } else {
            query = "SELECT c, cc.depth FROM Category c JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
                    "WHERE cc.ancestor.id = :parentId AND c.id != :parentId AND c.shop = :shop " +
                    "ORDER BY cc.depth, c.name";
            
            return em.createQuery(query, Object[].class)
                .setParameter("parentId", parentId)
                .setParameter("shop", shop)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
        }
    }

    public long countDescendants(Long categoryId, Shop shop) {
        return em.createQuery(
            "SELECT COUNT(cc) FROM CategoryClosure cc " +
            "WHERE cc.ancestor.id = :categoryId AND cc.descendant.id != :categoryId " +
            "AND cc.descendant.shop = :shop", Long.class)
            .setParameter("categoryId", categoryId)
            .setParameter("shop", shop)
            .getSingleResult();
    }
    
    @Transactional
    protected void addToClosureTable(Category category, Category parent) {
        // Add self-relationship
        CategoryClosure selfClosure = new CategoryClosure();
        selfClosure.id = new CategoryClosureId(category.id, category.id);
        selfClosure.ancestor = category;
        selfClosure.descendant = category;
        selfClosure.depth = 0;
        selfClosure.persist();
        
        // Add ancestor relationships
        if (parent != null) {
            List<CategoryClosure> parentClosures = CategoryClosure.list("descendant.id", parent.id);
            for (CategoryClosure closure : parentClosures) {
                CategoryClosure newClosure = new CategoryClosure();
                newClosure.id = new CategoryClosureId(closure.ancestor.id, category.id);
                newClosure.ancestor = closure.ancestor;
                newClosure.descendant = category;
                newClosure.depth = closure.depth + 1;
                newClosure.persist();
            }
        }
    }
}