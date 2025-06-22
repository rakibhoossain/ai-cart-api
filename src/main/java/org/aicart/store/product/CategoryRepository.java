package org.aicart.store.product;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aicart.store.product.entity.Category;
import jakarta.transaction.Transactional;
import org.aicart.store.product.entity.CategoryClosure;
import org.aicart.store.product.entity.CategoryClosureId;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {
    
    @Inject
    EntityManager em;
    
    public List<Category> getCategories(int page, int size) {

        return Category.find("parentCategory IS NULL")
                .page(page, size)
                .list();
    }

    @Transactional
    public Category addCategory(String name, Long parentId) {
        Category category = new Category();
        category.name = name;

        if (parentId != null) {
            Category parent = findById(parentId);
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
    public void  updateParent(Long categoryId, Long newParentId) {
        Category category = findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        Category newParent = newParentId != null ? findById(newParentId) : null;
        if (newParentId != null && newParent == null) {
            throw new IllegalArgumentException("New parent category not found");
        }

        category.parentCategory = newParent;
        category.persist();

        // Clear old closure table relationships
        getEntityManager()
                .createQuery("DELETE FROM CategoryClosure WHERE descendant.id IN " +
                        "(SELECT descendant.id FROM CategoryClosure WHERE ancestor.id = :categoryId)")
                .setParameter("categoryId", categoryId)
                .executeUpdate();

        // Rebuild closure table for the new hierarchy
        addToClosureTable(category, newParent);
    }



    @Transactional
    public void deleteCategory(Long id) {
        Category category = findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Reassign children to the deleted category's parent
        List<Category> children = list("parentCategory.id", id);
        for (Category child : children) {
            child.parentCategory = category.parentCategory;
            child.persist();
        }

        CategoryClosure.delete("descendant.id", id);
        delete("id", id);
    }


    public List<Category> getDescendants(Long categoryId) {
        return getEntityManager()
                .createQuery("SELECT cc.descendant FROM CategoryClosure cc WHERE cc.ancestor.id = :id", Category.class)
                .setParameter("id", categoryId)
                .getResultList();
    }

    public List<Category> getAncestors(Long categoryId) {
        return getEntityManager()
                .createQuery("SELECT cc.ancestor FROM CategoryClosure cc WHERE cc.descendant.id = :id", Category.class)
                .setParameter("id", categoryId)
                .getResultList();
    }

    private void addToClosureTable(Category category, Category parent) {
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

        // Add self-relationship
        CategoryClosure selfClosure = new CategoryClosure();
        selfClosure.id = new CategoryClosureId(category.id, category.id);
        selfClosure.ancestor = category;
        selfClosure.descendant = category;
        selfClosure.depth = 0;
        selfClosure.persist();
    }
    
    /**
     * Get categories with depth information relative to a parent
     * Useful for building hierarchical UIs
     */
    public List<Object[]> getCategoriesWithDepth(Long parentId, int page, int size) {
        String query = parentId == null ?
            "SELECT c, cc.depth FROM Category c JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
            "WHERE cc.ancestor.id = cc.descendant.id AND c.parentCategory IS NULL ORDER BY c.name" :
            "SELECT c, cc.depth FROM Category c JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
            "WHERE cc.ancestor.id = :parentId AND c.id != :parentId ORDER BY cc.depth, c.name";
            
        return em.createQuery(query, Object[].class)
            .setParameter("parentId", parentId)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
    }
    
    /**
     * Get entire category tree in a single query
     * Optimized for building complete category trees
     */
    public List<Object[]> getEntireCategoryTree() {
        return em.createQuery(
            "SELECT c, p, cc.depth FROM Category c " +
            "LEFT JOIN c.parentCategory p " +
            "JOIN CategoryClosure cc ON c.id = cc.descendant.id " +
            "WHERE cc.ancestor.id IN (SELECT c2.id FROM Category c2 WHERE c2.parentCategory IS NULL) " +
            "ORDER BY cc.depth, c.name", Object[].class)
            .getResultList();
    }
    
    /**
     * Move an entire subtree to a new parent
     * More efficient than updateParent for moving large subtrees
     */
    @Transactional
    public void moveSubtree(Long categoryId, Long newParentId) {
        Category category = findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        
        Category newParent = newParentId != null ? findById(newParentId) : null;
        if (newParentId != null && newParent == null) {
            throw new IllegalArgumentException("New parent category not found");
        }
        
        // Check if new parent is not a descendant of the category being moved
        if (newParentId != null) {
            boolean isDescendant = CategoryClosure.count(
                "ancestor.id = ?1 AND descendant.id = ?2", 
                categoryId, newParentId) > 0;
                
            if (isDescendant) {
                throw new IllegalArgumentException("Cannot move a category to its own descendant");
            }
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
     * Batch update multiple categories at once
     */
    @Transactional
    public void batchUpdateCategories(List<Category> categories) {
        for (Category category : categories) {
            if (category.id != null) {
                em.merge(category);
            } else {
                em.persist(category);
            }
        }
    }
    
    /**
     * Get categories with pagination and sorting
     */
    public List<Category> getCategories(int page, int size, String sortField, boolean ascending) {
        String direction = ascending ? "ASC" : "DESC";
        return find("ORDER BY " + sortField + " " + direction)
                .page(page, size)
                .list();
    }
    
    /**
     * Count descendants of a category
     */
    public long countDescendants(Long categoryId) {
        return CategoryClosure.count("ancestor.id = ?1 AND descendant.id != ?1", categoryId);
    }
}
