package org.aicart.store.product;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.aicart.store.product.entity.Category;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;
    
    // Add cache for root categories
    private List<Category> rootCategoriesCache;
    private long rootCategoriesCacheTime = 0;
    private static final long CACHE_EXPIRY_MS = 300000; // 5 minutes
    
    public List<Category> getCategories(int page, int size) {
        long now = System.currentTimeMillis();
        if (rootCategoriesCache == null || now - rootCategoriesCacheTime > CACHE_EXPIRY_MS) {
            rootCategoriesCache = categoryRepository.getCategories(page, size);
            rootCategoriesCacheTime = now;
        }
        return rootCategoriesCache;
    }
    
    // Add method with sorting parameters
    public List<Category> getCategories(int page, int size, String sortField, boolean ascending) {
        return categoryRepository.getCategories(page, size, sortField, ascending);
    }
    
    public void invalidateCache() {
        rootCategoriesCache = null;
    }
    
    // After any write operation (add, update, delete), call invalidateCache()
    
    public Category addCategory(String name, Long parentId) {
        Category result = categoryRepository.addCategory(name, parentId);
        invalidateCache();
        return result;
    }

    public void updateParent(Long id, Long newParentId) {
        categoryRepository.updateParent(id, newParentId);
        invalidateCache();
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteCategory(id);
        invalidateCache();
    }

    public List<Category> getDescendants(Long categoryId) {
        return categoryRepository.getDescendants(categoryId);
    }

    public List<Category> getAncestors(Long categoryId) {
        return categoryRepository.getAncestors(categoryId);
    }
    
    public void moveSubtree(Long categoryId, Long newParentId) {
        categoryRepository.moveSubtree(categoryId, newParentId);
        invalidateCache();
    }
    
    public List<Object[]> getCategoryTree() {
        return categoryRepository.getEntireCategoryTree();
    }
    
    // Add missing methods
    
    /**
     * Get categories with depth information relative to a parent
     */
    public List<Object[]> getCategoriesWithDepth(Long parentId, int page, int size) {
        return categoryRepository.getCategoriesWithDepth(parentId, page, size);
    }
    
    /**
     * Count descendants of a category
     */
    public long countDescendants(Long categoryId) {
        return categoryRepository.countDescendants(categoryId);
    }
}

