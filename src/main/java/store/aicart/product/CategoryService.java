package store.aicart.product;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import store.aicart.product.entity.Category;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    public List<Category> getCategories(int page, int size) {
        return categoryRepository.getCategories(page, size);
    }

    public Category addCategory(String name, Long parentId) {
        return categoryRepository.addCategory(name, parentId);
    }

    public void updateParent(Long id, Long newParentId) {
        categoryRepository.updateParent(id, newParentId);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteCategory(id);
    }

    public List<Category> getDescendants(Long categoryId) {
        return categoryRepository.getDescendants(categoryId);
    }

    public List<Category> getAncestors(Long categoryId) {
        return categoryRepository.getAncestors(categoryId);
    }
}

