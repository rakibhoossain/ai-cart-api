package org.aicart.store.product;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aicart.store.product.entity.Category;
import org.aicart.store.user.entity.Shop;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;
    
    // Add cache for root categories - will be updated to be shop-specific later
    private List<Category> rootCategoriesCache;
    private long rootCategoriesCacheTime = 0;
    private static final long CACHE_EXPIRY_MS = 300000; // 5 minutes
    
    public void invalidateCache() {
        rootCategoriesCache = null;
    }
    
    /**
     * Find a category by ID
     */
    public Category findById(Long id, Shop shop) {
        return categoryRepository.findById(id, shop);
    }

    /**
     * Get categories with pagination, sorting, and optional search
     */
    public List<Category> getCategories(int page, int size, String sortField, boolean ascending, String searchQuery, Shop shop) {
        return categoryRepository.getCategories(page, size, sortField, ascending, searchQuery, shop);
    }

    /**
     * Count total number of categories with optional filtering
     */
    public long countCategories(String sortField, boolean ascending, String searchQuery, Shop shop) {
        return categoryRepository.countCategories(searchQuery, shop);
    }

    /**
     * Add a category
     */
    public Category addCategory(String name, Long parentId, Shop shop) {
        Category result = categoryRepository.addCategory(name, parentId, shop);
        invalidateCache();
        return result;
    }

    /**
     * Update parent category
     */
    public void updateParent(Long id, Long newParentId, Shop shop) {
        categoryRepository.updateParent(id, newParentId, shop);
        invalidateCache();
    }

    /**
     * Delete a category
     */
    public void deleteCategory(Long id, Shop shop) {
        categoryRepository.deleteCategory(id, shop);
        invalidateCache();
    }

    /**
     * Get descendants of a category
     */
    public List<Category> getDescendants(Long categoryId, Shop shop) {
        return categoryRepository.getDescendants(categoryId, shop);
    }

    /**
     * Get ancestors of a category
     */
    public List<Category> getAncestors(Long categoryId, Shop shop) {
        return categoryRepository.getAncestors(categoryId, shop);
    }

    /**
     * Move a subtree
     */
    public void moveSubtree(Long categoryId, Long newParentId, Shop shop) {
        categoryRepository.moveSubtree(categoryId, newParentId, shop);
        invalidateCache();
    }

    /**
     * Get category tree
     */
    public List<Object[]> getCategoryTree(Shop shop) {
        return categoryRepository.getEntireCategoryTree(shop);
    }

    /**
     * Get categories with depth information relative to a parent
     */
    public List<Object[]> getCategoriesWithDepth(Long parentId, int page, int size, Shop shop) {
        return categoryRepository.getCategoriesWithDepth(parentId, page, size, shop);
    }

    /**
     * Count descendants of a category
     */
    public long countDescendants(Long categoryId, Shop shop) {
        return categoryRepository.countDescendants(categoryId, shop);
    }
}

